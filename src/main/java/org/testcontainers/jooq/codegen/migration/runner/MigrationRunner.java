package org.testcontainers.jooq.codegen.migration.runner;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Common facade for both migration tools
 */
public interface MigrationRunner {

    void run(RunnerProperties properties) throws MojoExecutionException, MojoFailureException;
}
