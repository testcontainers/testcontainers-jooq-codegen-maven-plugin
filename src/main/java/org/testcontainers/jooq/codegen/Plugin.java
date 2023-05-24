package org.testcontainers.jooq.codegen;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.jooq.Constants.XSD_CODEGEN;
import static org.jooq.codegen.GenerationTool.DEFAULT_TARGET_DIRECTORY;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.jooq.codegen.database.DatabaseProps;
import org.testcontainers.jooq.codegen.database.DatabaseProvider;
import org.testcontainers.jooq.codegen.migration.runner.*;

/**
 * Plugin entry point.
 */
@Mojo(name = "generate", defaultPhase = GENERATE_SOURCES, requiresDependencyResolution = TEST, threadSafe = true)
public class Plugin extends AbstractMojo {
    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "jooq.codegen.skip")
    private boolean skip;

    @Parameter(property = "jooq.codegen.basedir")
    private String basedir;

    @Parameter
    private org.jooq.meta.jaxb.Jdbc jdbc;

    @Parameter
    private org.jooq.meta.jaxb.Generator generator;

    @Parameter
    private DatabaseProps database;

    @Parameter
    private FlywayRunner flyway;

    @Parameter
    private LiquibaseRunner liquibase;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping jOOQ code generation");
            return;
        }

        checkGeneratorArguments();

        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        URLClassLoader mavenClassloader = getMavenClassloader();

        JdbcDatabaseContainer<?> container = null;
        try {
            String jdbcUrl;
            String username;
            String password;
            Driver driver;

            // If jdbc details are provided, then connect to that existing database.
            // Otherwise, spin up the database using Testcontainers
            if (this.jdbc == null
                    || this.jdbc.getUrl() == null
                    || this.jdbc.getUsername() == null
                    || this.jdbc.getPassword() == null) {
                container = DatabaseProvider.getDatabaseContainer(database);
                container.start();

                jdbcUrl = container.getJdbcUrl();
                username = container.getUsername();
                password = container.getPassword();
                driver = container.getJdbcDriverInstance();
            } else {
                jdbcUrl = this.jdbc.getUrl();
                username = this.jdbc.getUsername();
                password = this.jdbc.getPassword();
                driver = DriverManager.getDriver(jdbcUrl);
            }

            Thread.currentThread().setContextClassLoader(mavenClassloader);
            String actualBasedir = basedir == null ? project.getBasedir().getAbsolutePath() : basedir;

            if (generator.getTarget() == null) {
                generator.setTarget(new Target());
            }
            if (generator.getTarget().getDirectory() == null) {
                generator.getTarget().setDirectory(DEFAULT_TARGET_DIRECTORY);
            }

            Jdbc jdbc = new Jdbc().withUrl(jdbcUrl).withUsername(username).withPassword(password);

            Optional<MigrationRunner> oFlyway = Optional.ofNullable(flyway);
            Optional<MigrationRunner> oLiquibase = Optional.ofNullable(liquibase);
            if (oFlyway.isPresent() && oLiquibase.isPresent()) {
                getLog().error(
                                """
                                Incorrect configuration is provided.Plugin supports only one migration tool.
                                Please remain only flyway or liquibase.""");
                throw new MojoExecutionException(
                        "Both configurations for migration tool are provided, pick either flyway or liquibase");
            }

            RunnerProperties properties =
                    new RunnerProperties(jdbcUrl, username, password, driver, mavenClassloader, project);
            oLiquibase
                    .or(() -> oFlyway)
                    .orElseThrow(() -> new IllegalArgumentException("Neither liquibase nor flyway provided!"))
                    .run(properties);

            getLog().info("Migrations applied successfully");

            Configuration configuration = new Configuration();
            configuration.setJdbc(jdbc);
            configuration.setGenerator(generator);
            configuration.setBasedir(actualBasedir);

            if (getLog().isDebugEnabled()) {
                getLog().debug("Using this configuration:\n" + configuration);
            }
            GenerationTool.generate(configuration);
        } catch (Exception ex) {
            throw new MojoExecutionException("Error running jOOQ code generation tool", ex);
        } finally {
            try {
                if (container != null) {
                    container.stop();
                }
            } catch (Throwable e) {
                getLog().error("Couldn't stop the container.", e);
            }

            Thread.currentThread().setContextClassLoader(oldCL);
            try {
                mavenClassloader.close();
            } catch (Throwable e) {
                getLog().error("Couldn't close the classloader.", e);
            }
        }
        project.addCompileSourceRoot(generator.getTarget().getDirectory());
    }

    private void checkGeneratorArguments() throws MojoExecutionException {
        if (generator == null) {
            getLog().error("Incorrect configuration of jOOQ code generation tool");
            getLog().error(
                            """
                            The jOOQ-codegen-maven module's generator configuration is not set up correctly.
                            This can have a variety of reasons, among which:
                            - Your pom.xml's <configuration> contains invalid XML according to %s
                            - There is a version or artifact mismatch between your pom.xml and your commandline"""
                                    .formatted(XSD_CODEGEN));

            throw new MojoExecutionException(
                    "Incorrect configuration of jOOQ code generation tool. See error above for details.");
        }
    }

    private URLClassLoader getMavenClassloader() throws MojoExecutionException {
        try {
            List<String> classpathElements = project.getRuntimeClasspathElements();
            URL urls[] = new URL[classpathElements.size()];

            for (int i = 0; i < urls.length; i++) {
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();
            }

            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (Exception e) {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }
}
