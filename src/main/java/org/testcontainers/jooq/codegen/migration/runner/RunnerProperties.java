package org.testcontainers.jooq.codegen.migration.runner;

import java.net.URLClassLoader;
import java.sql.Driver;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;

/**
 * Properties for running migration and generation sources
 *
 * @param log              Maven logger
 * @param mavenProject     Target project
 * @param mavenClassloader Maven classloader
 * @param targetDatasource Datasource to migrate and generate sources on
 */
public record RunnerProperties(
        Log log, MavenProject mavenProject, URLClassLoader mavenClassloader, TargetDatasource targetDatasource) {
    public String getUrl() {
        return targetDatasource.getUrl();
    }

    public String getUsername() {
        return targetDatasource.getUsername();
    }

    public String getPassword() {
        return targetDatasource.getPassword();
    }

    public Driver getDriverInstance() {
        return targetDatasource.getDriverInstance();
    }
}
