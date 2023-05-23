package org.testcontainers.jooq.codegen.migration.runner;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.resource.SearchPathResourceAccessor;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Liquibase runner
 */
public class LiquibaseRunner implements MigrationRunner {

    @Parameter(name = "liquibase.changeLogPath", required = true)
    private String changeLogPath;

    /**
     * Optional <br/>
     * Default - project.basedir
     */
    @Parameter(name = "liquibase.changeLogDirectory")
    private String changeLogDirectory;

    @Override
    public void run(RunnerProperties runnerProperties) throws MojoExecutionException {
        try {
            Driver driver = runnerProperties.driver();
            Properties properties = new Properties();
            properties.put("user", runnerProperties.username());
            properties.put("password", runnerProperties.password());
            Connection c = driver.connect(runnerProperties.jdbcUrl(), properties);
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
            ResourceAccessor accessor = getResourceAccessor(runnerProperties);
            Liquibase liquibase = new Liquibase(changeLogPath, accessor, database);
            liquibase.update();
        } catch (Exception e) {
            throw new MojoExecutionException(e);
        }
    }

    private ResourceAccessor getResourceAccessor(RunnerProperties properties) throws FileNotFoundException {
        List<ResourceAccessor> resourceAccessors = new ArrayList<>();
        resourceAccessors.add(new ClassLoaderResourceAccessor(properties.mavenClassloader()));
        resourceAccessors.add(new ClassLoaderResourceAccessor(getClass().getClassLoader()));
        resourceAccessors.add(
                new DirectoryResourceAccessor(properties.mavenProject().getBasedir()));
        ResourceAccessor[] array = resourceAccessors.toArray(ResourceAccessor[]::new);
        return new SearchPathResourceAccessor(changeLogDirectory, array);
    }
}
