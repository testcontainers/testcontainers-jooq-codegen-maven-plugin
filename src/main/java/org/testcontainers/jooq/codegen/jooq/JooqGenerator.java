package org.testcontainers.jooq.codegen.jooq;

import static org.jooq.Constants.XSD_CODEGEN;
import static org.jooq.codegen.GenerationTool.DEFAULT_TARGET_DIRECTORY;

import java.util.Optional;
import javax.inject.Inject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.jetbrains.annotations.NotNull;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;
import org.testcontainers.jooq.codegen.migration.runner.RunnerProperties;

/**
 * Jooq sources generator
 */
public class JooqGenerator {

    @Inject
    private MavenProject project;

    public void generateSources(Log log, TargetDatasource targetDatasource, JooqProps jooq) throws Exception {
        var configuration = getConfiguration(log, jooq);

        var jdbc = jooq.getJdbc();
        if (jdbc == null) {
            jdbc = new Jdbc();
        }

        jdbc.setUrl(targetDatasource.getUrl());
        jdbc.setUser(targetDatasource.getUsername());
        jdbc.setPassword(targetDatasource.getPassword());

        configuration.setJdbc(jdbc);

        if (log.isDebugEnabled()) {
            log.debug("Using this configuration:\n" + configuration);
        }
        GenerationTool.generate(configuration);
        project.addCompileSourceRoot(jooq.getGenerator().getTarget().getDirectory());
    }

    public Configuration getConfiguration(Log log, JooqProps jooq) throws MojoExecutionException {
        checkGeneratorArguments(jooq, log);
        setGeneratorTargets(jooq);

        var configuration = new Configuration();
        configuration.setGenerator(jooq.getGenerator());

        var basedir = Optional.ofNullable(jooq.getBaseDir())
            .orElse(project.getBasedir().getAbsolutePath());
        configuration.setBasedir(basedir);

        return configuration;
    }

    private void setGeneratorTargets(JooqProps jooq) {
        var generator = jooq.getGenerator();
        if (generator.getTarget() == null) {
            generator.setTarget(new Target());
        }
        if (generator.getTarget().getDirectory() == null) {
            generator.getTarget().setDirectory(DEFAULT_TARGET_DIRECTORY);
        }
    }

    private void checkGeneratorArguments(JooqProps jooq, Log log) throws MojoExecutionException {
        if (jooq.getGenerator() != null) {
            return;
        }

        log.error("Incorrect configuration of jOOQ code generation tool");
        log.error(
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
