package org.testcontainers.jooq.codegen;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.testcontainers.jooq.codegen.database.DatabaseProps;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;
import org.testcontainers.jooq.codegen.jooq.JooqGenerator;
import org.testcontainers.jooq.codegen.jooq.JooqProps;
import org.testcontainers.jooq.codegen.migration.runner.FlywayRunner;
import org.testcontainers.jooq.codegen.migration.runner.LiquibaseRunner;
import org.testcontainers.jooq.codegen.migration.runner.MigrationRunner;
import org.testcontainers.jooq.codegen.migration.runner.RunnerProperties;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.maven.plugins.annotations.LifecyclePhase.GENERATE_SOURCES;
import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;
import static org.testcontainers.jooq.codegen.util.OptionalUtils.bothPresent;

/**
 * Plugin entry point.
 */
@Mojo(name = "generate", defaultPhase = GENERATE_SOURCES, requiresDependencyResolution = TEST, threadSafe = true)
public class Plugin extends AbstractMojo {

    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    @Parameter
    private boolean skip;

    /**
     * Location of the output directory.
     */
    @Parameter(name = "output", property = "testcontainers.jooq.codegen.maven.plugin.output",
        defaultValue = "${project.build.directory}/generated-sources/testcontainers")
    private File output;

    @Parameter(defaultValue = "true")
    private boolean skipIfInputIsUnchanged;

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

        MigrationRunner migrationRunner = getMigrationRunner();

        RunnerProperties properties = getRunnerProperties(mavenClassloader);
        Thread.currentThread().setContextClassLoader(mavenClassloader);

        GenerationTool.copyLarge()

        String currentChecksum = getCurrentChecksum(migrationRunner, properties);

        try {
            Path checksumPath = Path.of(output.getPath(), ".testcontainers-jooq-codegen.checksum");

            if (changed(checksumPath, currentChecksum)) {
                try (var targetDatasource = TargetDatasource.createOrJoinExisting(jooq, database)) {
                    doExecute(targetDatasource, migrationRunner, properties);
                }

                Files.createDirectories(checksumPath.getParent());
                Files.writeString(checksumPath, currentChecksum);
            }
        } catch (Exception ex) {
            throw new MojoExecutionException("Error running jOOQ code generation tool", ex);
        } finally {
            closeClassloader(oldCL, mavenClassloader);
        }
    }

    private String getCurrentChecksum(MigrationRunner migrationRunner, RunnerProperties properties) throws MojoExecutionException {
        String jooq = getJooqConfigurationChecksum();
        String changesets = getChangesetsChecksum(migrationRunner, properties);
        return jooq + changesets;
    }

    @SuppressWarnings("UnstableApiUsage")
    private String getJooqConfigurationChecksum() throws MojoExecutionException {
        Configuration configuration = jooqGenerator.getConfiguration(getLog(), jooq);
        String configurationXML = configuration.toString();
        return Hashing.sha256()
                .hashString(configurationXML, Charsets.UTF_8)
                .toString();
    }

    private static String getChangesetsChecksum(MigrationRunner migrationRunner, RunnerProperties properties) throws MojoExecutionException {
        return migrationRunner.checksum(properties);
    }

    private boolean changed(Path checksumPath, String currentChecksum) throws IOException {
        if (Files.exists(checksumPath)) {
            String storedChecksum = Files.readString(checksumPath);
            if (Objects.equals(storedChecksum, currentChecksum)) {
                getLog().info("Migration is not required, skipping");
                return false;
            } else {
                getLog().debug(String.format("Migration checksum [%s] differs from stored checksum [%s]", currentChecksum, storedChecksum));
                return true;
            }
        } else {
            getLog().debug(String.format("Migration checksum [%s] differs from stored checksum [none]", currentChecksum));
            return true;
        }
    }

    private void doExecute(TargetDatasource targetDatasource, MigrationRunner migrationRunner, RunnerProperties properties) throws Exception {
        migrationRunner.run(properties, targetDatasource);

        getLog().info("Migration completed");

        jooqGenerator.generateSources(getLog(), targetDatasource, jooq);
    }

    private MigrationRunner getMigrationRunner() throws MojoExecutionException {
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

        return oLiquibase
                .or(() -> oFlyway)
                .orElseThrow(() -> new IllegalArgumentException("Neither liquibase nor flyway provided!"));
    }

    private RunnerProperties getRunnerProperties(URLClassLoader mavenClassloader) {
        return RunnerProperties.builder()
                .mavenProject(project)
                .mavenClassloader(mavenClassloader)
                .build();
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
