package org.testcontainers.jooq.codegen;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.testcontainers.jooq.codegen.util.OptionalUtils.bothPresent;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.testcontainers.jooq.codegen.database.DatabaseProps;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;
import org.testcontainers.jooq.codegen.jooq.JooqGenerator;
import org.testcontainers.jooq.codegen.jooq.JooqProps;
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

    @Parameter
    private boolean skip;

    @Parameter(required = true)
    private DatabaseProps database;

    @Parameter(required = true)
    private JooqProps jooq;

    @Parameter
    private FlywayRunner flyway;

    @Parameter
    private LiquibaseRunner liquibase;

    @Inject
    private JooqGenerator jooqGenerator;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("'Skip' is true, Skipping jOOQ code generation");
            return;
        }

        if (database.getType() == null) {
            throw new MojoExecutionException("Property 'type' should be specified inside 'database' block");
        }

        final var oldCL = Thread.currentThread().getContextClassLoader();
        final var mavenClassloader = getMavenClassloader();

        try (var targetDatasource = TargetDatasource.createOrJoinExisting(jooq, database)) {
            doExecute(mavenClassloader, targetDatasource);
        } catch (Exception ex) {
            throw new MojoExecutionException("Error running jOOQ code generation tool", ex);
        } finally {
            closeClassloader(oldCL, mavenClassloader);
        }
    }

    private void doExecute(URLClassLoader mavenClassloader, TargetDatasource targetDatasource) throws Exception {
        var properties = RunnerProperties.builder()
                .targetDatasource(targetDatasource)
                .mavenProject(project)
                .log(getLog())
                .mavenClassloader(mavenClassloader)
                .build();
        Thread.currentThread().setContextClassLoader(mavenClassloader);

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

        getLog().info("Migration completed");

        jooqGenerator.generateSources(properties, jooq);
    }

    private void closeClassloader(ClassLoader oldCL, URLClassLoader mavenClassloader) {
        Thread.currentThread().setContextClassLoader(oldCL);
        try {
            mavenClassloader.close();
        } catch (Throwable e) {
            getLog().error("Couldn't close the classloader.", e);
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
