package org.testcontainers.jooq.codegen.migration.runner;

import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.Objects;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;

/**
 * Properties for running migration and generation sources
 */
public final class RunnerProperties {
    /**
     * Maven logger
     */
    private final Log log;
    /**
     * Target project
     */
    private final MavenProject mavenProject;
    /**
     * Maven classloader
     */
    private final URLClassLoader mavenClassloader;

    /**
     * Datasource to migrate and generate sources on
     */
    private final TargetDatasource targetDatasource;

    private RunnerProperties(
            Log log, MavenProject mavenProject, URLClassLoader mavenClassloader, TargetDatasource targetDatasource) {
        this.log = log;
        this.mavenProject = mavenProject;
        this.mavenClassloader = mavenClassloader;
        this.targetDatasource = targetDatasource;
    }

    public Log log() {
        return log;
    }

    public MavenProject mavenProject() {
        return mavenProject;
    }

    public URLClassLoader mavenClassloader() {
        return mavenClassloader;
    }

    public TargetDatasource targetDatasource() {
        return targetDatasource;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunnerProperties that = (RunnerProperties) o;
        return Objects.equals(log, that.log)
                && Objects.equals(mavenProject, that.mavenProject)
                && Objects.equals(mavenClassloader, that.mavenClassloader)
                && Objects.equals(targetDatasource, that.targetDatasource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(log, mavenProject, mavenClassloader, targetDatasource);
    }

    @Override
    public String toString() {
        return "RunnerProperties{" + "log="
                + log + ", mavenProject="
                + mavenProject + ", mavenClassloader="
                + mavenClassloader + ", targetDatasource="
                + targetDatasource + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Log log;
        private MavenProject mavenProject;
        private URLClassLoader mavenClassloader;
        private TargetDatasource targetDatasource;

        public Builder log(Log log) {
            this.log = log;
            return this;
        }

        public Builder mavenProject(MavenProject mavenProject) {
            this.mavenProject = mavenProject;
            return this;
        }

        public Builder mavenClassloader(URLClassLoader mavenClassloader) {
            this.mavenClassloader = mavenClassloader;
            return this;
        }

        public Builder targetDatasource(TargetDatasource targetDatasource) {
            this.targetDatasource = targetDatasource;
            return this;
        }

        public RunnerProperties build() {
            return new RunnerProperties(log, mavenProject, mavenClassloader, targetDatasource);
        }
    }
}
