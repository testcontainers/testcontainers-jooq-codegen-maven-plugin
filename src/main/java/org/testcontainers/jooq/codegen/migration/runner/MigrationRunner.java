package org.testcontainers.jooq.codegen.migration.runner;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;

/** Common facade for both migration tools */
public interface MigrationRunner {

    String checksum(RunnerProperties runnerProps) throws MojoExecutionException;

    void run(RunnerProperties properties, TargetDatasource targetDatasource) throws MojoExecutionException, MojoFailureException;
}
