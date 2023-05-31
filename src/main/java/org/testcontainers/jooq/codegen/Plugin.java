package org.testcontainers.jooq.codegen;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.jooq.Constants.XSD_CODEGEN;
import static org.jooq.codegen.GenerationTool.DEFAULT_TARGET_DIRECTORY;
import static org.testcontainers.jooq.codegen.util.OptionalUtils.bothPresent;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
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
import org.testcontainers.jooq.codegen.database.DatabaseProps;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;
import org.testcontainers.jooq.codegen.migration.runner.FlywayRunner;
import org.testcontainers.jooq.codegen.migration.runner.LiquibaseRunner;
import org.testcontainers.jooq.codegen.migration.runner.MigrationRunner;
import org.testcontainers.jooq.codegen.migration.runner.RunnerProperties;

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

    @Parameter(required = true)
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

        if (database.getType() == null) {
            throw new MojoExecutionException("Property 'type' should be specified inside 'database' block");
        }
        checkGeneratorArguments();
        final var oldCL = Thread.currentThread().getContextClassLoader();
        final var mavenClassloader = getMavenClassloader();

        try (var targetDatasource = TargetDatasource.createOrJoinExisting(jdbc, database)) {
            doExecute(mavenClassloader, targetDatasource);
        } catch (Exception ex) {
            throw new MojoExecutionException("Error running jOOQ code generation tool", ex);
        } finally {
            closeClassloader(oldCL, mavenClassloader);
        }
        project.addCompileSourceRoot(generator.getTarget().getDirectory());
    }

    private void doExecute(URLClassLoader mavenClassloader, TargetDatasource targetDatasource) throws Exception {
        var properties = new RunnerProperties(
                targetDatasource.getUrl(),
                targetDatasource.getUsername(),
                targetDatasource.getPassword(),
                targetDatasource.getDriver(),
                mavenClassloader,
                project);
        Thread.currentThread().setContextClassLoader(mavenClassloader);
        String actualBasedir = basedir == null ? project.getBasedir().getAbsolutePath() : basedir;

        setGeneratorTargets();

        if (jdbc == null) {
            jdbc = new Jdbc();
        }
        jdbc.setUrl(targetDatasource.getUrl());
        jdbc.setUser(targetDatasource.getUsername());
        jdbc.setPassword(targetDatasource.getPassword());

        final var oFlyway = Optional.<MigrationRunner>ofNullable(flyway);
        final var oLiquibase = Optional.<MigrationRunner>ofNullable(liquibase);
        if (bothPresent(oFlyway, oLiquibase)) {
            getLog().error(
                            """
                            Incorrect configuration is provided.Plugin supports only one migration tool.
                            Please remain only flyway or liquibase.""");
            throw new MojoExecutionException(
                    "Both configurations for migration tool are provided, pick either flyway or liquibase");
        }

        oLiquibase
                .or(() -> oFlyway)
                .orElseThrow(() -> new IllegalArgumentException("Neither liquibase nor flyway provided!"))
                .run(properties);

        getLog().info("Migrations applied successfully");

        final var configuration = new Configuration();
        configuration.setJdbc(jdbc);
        configuration.setGenerator(generator);
        configuration.setBasedir(actualBasedir);

        if (getLog().isDebugEnabled()) {
            getLog().debug("Using this configuration:\n" + configuration);
        }
        GenerationTool.generate(configuration);
    }

    private void closeClassloader(ClassLoader oldCL, URLClassLoader mavenClassloader) {
        Thread.currentThread().setContextClassLoader(oldCL);
        try {
            mavenClassloader.close();
        } catch (Throwable e) {
            getLog().error("Couldn't close the classloader.", e);
        }
    }

    private void setGeneratorTargets() {
        if (generator.getTarget() == null) {
            generator.setTarget(new Target());
        }
        if (generator.getTarget().getDirectory() == null) {
            generator.getTarget().setDirectory(DEFAULT_TARGET_DIRECTORY);
        }
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
            if (classpathElements == null) {
                classpathElements = List.of();
            }
            URL[] urls = new URL[classpathElements.size()];

            for (int i = 0; i < urls.length; i++) {
                urls[i] = new File(classpathElements.get(i)).toURI().toURL();
            }

            return new URLClassLoader(urls, getClass().getClassLoader());
        } catch (Exception e) {
            throw new MojoExecutionException("Couldn't create a classloader.", e);
        }
    }
}
