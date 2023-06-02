package org.testcontainers.jooq.codegen.migration.runner;

import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.Objects;
import org.apache.maven.project.MavenProject;

public final class RunnerProperties {
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final Driver driver;
    private final URLClassLoader mavenClassloader;
    private final MavenProject mavenProject;

    public RunnerProperties(
            String jdbcUrl,
            String username,
            String password,
            Driver driver,
            URLClassLoader mavenClassloader,
            MavenProject mavenProject) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driver = driver;
        this.mavenClassloader = mavenClassloader;
        this.mavenProject = mavenProject;
    }

    public String jdbcUrl() {
        return jdbcUrl;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public Driver driver() {
        return driver;
    }

    public URLClassLoader mavenClassloader() {
        return mavenClassloader;
    }

    public MavenProject mavenProject() {
        return mavenProject;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RunnerProperties) obj;
        return Objects.equals(this.jdbcUrl, that.jdbcUrl)
                && Objects.equals(this.username, that.username)
                && Objects.equals(this.password, that.password)
                && Objects.equals(this.driver, that.driver)
                && Objects.equals(this.mavenClassloader, that.mavenClassloader)
                && Objects.equals(this.mavenProject, that.mavenProject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jdbcUrl, username, password, driver, mavenClassloader, mavenProject);
    }

    @Override
    public String toString() {
        return "RunnerProperties[" + "jdbcUrl=" + jdbcUrl + ", " + "username=" + username + ", "
                + "password=" + password + ", " + "driver=" + driver + ", " + "mavenClassloader="
                + mavenClassloader + ", " + "mavenProject=" + mavenProject + ']';
    }
}
