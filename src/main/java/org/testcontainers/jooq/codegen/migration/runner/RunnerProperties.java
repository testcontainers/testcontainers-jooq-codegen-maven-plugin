package org.testcontainers.jooq.codegen.migration.runner;

import java.net.URLClassLoader;
import java.sql.Driver;
import org.apache.maven.project.MavenProject;

public record RunnerProperties(String jdbcUrl, String username, String password, Driver driver,
                               URLClassLoader mavenClassloader, MavenProject mavenProject) {
}
