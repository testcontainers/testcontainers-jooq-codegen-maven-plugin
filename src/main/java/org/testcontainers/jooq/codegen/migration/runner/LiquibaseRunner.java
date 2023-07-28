package org.testcontainers.jooq.codegen.migration.runner;

import static java.util.Optional.ofNullable;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
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

    /**
     * Default: {@link #changeLogDirectory}/{@link #changeLogPath db.changelog-root.xml}<br/>
     * or src/main/resources/{@link #changeLogPath db.changelog-root.xml} <br/>
     * depending on {@link #changeLogDirectory} presence <br/>
     * Example: src/main/resources/db/changelog/db.changelog-root.xml
     */
    @Parameter(name = "liquibase.changeLogPath")
    private String changeLogPath;

    /**
     * Default: project.basedir
     */
    @Parameter(name = "liquibase.changeLogDirectory")
    private String changeLogDirectory;

    @Parameter(name = "liquibase.parameters")
    private Map<String, String> parameters;

    @Parameter(name = "liquibase.defaultSchemaName")
    private String defaultSchemaName;

    @Parameter(name = "liquibase.liquibaseSchemaName")
    private String liquibaseSchemaName;

    @Parameter(name = "liquibase.databaseChangeLogTableName")
    private String databaseChangeLogTableName;

    @Parameter(name = "liquibase.databaseChangeLogLockTableName")
    private String databaseChangeLogLockTableName;

    @Override
    public void run(RunnerProperties runnerProps) throws MojoExecutionException {
        setDefaultLocations(runnerProps);
        try {
            Driver driver = runnerProps.getDriverInstance();
            Properties properties = new Properties();
            properties.put("user", runnerProps.getUsername());
            properties.put("password", runnerProps.getPassword());
            Connection c = driver.connect(runnerProps.getUrl(), properties);
            Database database = createDatabase(c);
            ResourceAccessor accessor = getResourceAccessor(runnerProps);
            Liquibase liquibase = new Liquibase(changeLogPath, accessor, database);
            setParameters(liquibase);
            liquibase.update();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage());
        }
    }

    private void setDefaultLocations(RunnerProperties runnerProps) {
        var defaultDir = runnerProps.mavenProject().getBasedir();
        if (changeLogPath == null) {
            changeLogPath = changeLogDirectory == null
                    ? "src/main/resources/db/changelog/db.changelog-root.xml"
                    : "db.changelog-root.xml";
        }
        if (changeLogDirectory == null) {
            changeLogDirectory = defaultDir.getAbsolutePath();
        } else {
            changeLogDirectory =
                    defaultDir.toPath().resolve(changeLogDirectory).toFile().getAbsolutePath();
        }
    }

    private Database createDatabase(Connection c) throws DatabaseException {
        Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
        if (defaultSchemaName != null) {
            database.setDefaultSchemaName(defaultSchemaName);
        }
        ofNullable(liquibaseSchemaName).ifPresent(database::setLiquibaseSchemaName);
        ofNullable(databaseChangeLogLockTableName).ifPresent(database::setDatabaseChangeLogLockTableName);
        ofNullable(databaseChangeLogTableName).ifPresent(database::setDatabaseChangeLogTableName);
        return database;
    }

    private void setParameters(Liquibase liquibase) {
        if (parameters != null) {
            parameters.forEach(liquibase::setChangeLogParameter);
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
