package org.testcontainers.jooq.codegen.jooq;

import static org.jooq.Constants.XSD_CODEGEN;
import static org.jooq.codegen.GenerationTool.DEFAULT_TARGET_DIRECTORY;

import java.util.Optional;
import javax.inject.Inject;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;

/**
 * Jooq sources generator
 */
public class JooqGenerator {

    @Inject
    private MavenProject project;

    public void generateSources(TargetDatasource targetDatasource, JooqProps jooq, Log log) throws Exception {
        checkGeneratorArguments(jooq, log);
        setGeneratorTargets(jooq);

        var jdbc = jooq.getJdbc();
        var basedir = Optional.ofNullable(jooq.getBaseDir())
                .orElse(project.getBasedir().getAbsolutePath());
        if (jdbc == null) {
            jdbc = new Jdbc();
        }
        jdbc.setUrl(targetDatasource.getUrl());
        jdbc.setUser(targetDatasource.getUsername());
        jdbc.setPassword(targetDatasource.getPassword());

        final var configuration = new Configuration();
        configuration.setJdbc(jdbc);
        configuration.setGenerator(jooq.getGenerator());
        configuration.setBasedir(basedir);

        if (log.isDebugEnabled()) {
            log.debug("Using this configuration:\n" + configuration);
        }
        GenerationTool.generate(configuration);
        project.addCompileSourceRoot(jooq.getGenerator().getTarget().getDirectory());
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
        log.error(String.format("The jOOQ-codegen-maven module's generator configuration is not set up correctly.\n"
                + "This can have a variety of reasons, among which:\n"
                + "- Your pom.xml's <configuration> contains invalid XML according to %s\n"
                + "- There is a version or artifact mismatch between your pom.xml and your commandline\n", XSD_CODEGEN));

        throw new MojoExecutionException(
                "Incorrect configuration of jOOQ code generation tool. See error above for details.");
    }
}
