package com.sivalabs.jooq.codegen;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.jooq.Constants.XSD_CODEGEN;
import static org.jooq.codegen.GenerationTool.DEFAULT_TARGET_DIRECTORY;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
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
    private DatabaseType dbType;

    @Parameter
    private FlywayProps flyway;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Skipping jOOQ code generation");
            return;
        }

        if (generator == null) {
            getLog().error("Incorrect configuration of jOOQ code generation tool");
            getLog().error("\n"
                    + "The jOOQ-codegen-maven module's generator configuration is not set up correctly.\n"
                    + "This can have a variety of reasons, among which:\n"
                    + "- Your pom.xml's <configuration> contains invalid XML according to " + XSD_CODEGEN + "\n"
                    + "- There is a version or artifact mismatch between your pom.xml and your commandline");

            throw new MojoExecutionException(
                    "Incorrect configuration of jOOQ code generation tool. See error above for details.");
        }

        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        URLClassLoader pluginClassLoader = getClassLoader();

        JdbcDatabaseContainer<?> container = null;
        try {
            String jdbcUrl;
            String username;
            String password;

            if (this.jdbc == null
                    || this.jdbc.getUrl() == null
                    || this.jdbc.getUsername() == null
                    || this.jdbc.getPassword() == null) {
                container = DatabaseProvider.getDatabaseContainer(dbType);
                container.start();

                jdbcUrl = container.getJdbcUrl();
                username = container.getUsername();
                password = container.getPassword();
            } else {
                jdbcUrl = this.jdbc.getUrl();
                username = this.jdbc.getUsername();
                password = this.jdbc.getPassword();
            }

            if (flyway == null) {
                flyway = new FlywayProps();
            }
            this.flyway.setJdbcUrl(jdbcUrl);
            this.flyway.setUsername(username);
            this.flyway.setPassword(password);

            PluginProps pluginProps = new PluginProps();
            pluginProps.setDbType(dbType);
            pluginProps.setFlyway(flyway);

            Thread.currentThread().setContextClassLoader(pluginClassLoader);
            String actualBasedir = basedir == null ? project.getBasedir().getAbsolutePath() : basedir;

            if (generator.getTarget() == null) {
                generator.setTarget(new Target());
            }
            if (generator.getTarget().getDirectory() == null) {
                generator.getTarget().setDirectory(DEFAULT_TARGET_DIRECTORY);
            }

            Jdbc jdbc = new Jdbc().withUrl(jdbcUrl).withUsername(username).withPassword(password);

            if (flyway.getLocations() != null && !flyway.getLocations().isEmpty()) {
                FlywayMigrationRunner flywayRunner = new FlywayMigrationRunner(flyway);
                flywayRunner.run();
                getLog().info("Flyway migrations applied successfully");
            }

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
                pluginClassLoader.close();
            } catch (Throwable e) {
                getLog().error("Couldn't close the classloader.", e);
            }
        }
        project.addCompileSourceRoot(generator.getTarget().getDirectory());
    }

    private URLClassLoader getClassLoader() throws MojoExecutionException {
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
