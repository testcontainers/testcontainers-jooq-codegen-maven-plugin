package org.testcontainers.jooq.codegen.migration.runner;

import liquibase.Liquibase;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.DirectoryResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.resource.SearchPathResourceAccessor;
import liquibase.util.MD5Util;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static java.util.Optional.ofNullable;

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
    public String checksum(RunnerProperties runnerProps) throws MojoExecutionException {
        setDefaultLocations(runnerProps);

        try (Liquibase liquibase = setupLiquibase(runnerProps)) {
            DatabaseChangeLog changeLog = liquibase.getDatabaseChangeLog();

            List<String> checksums = changeLog.getChangeSets().stream()
                .map(it -> it.generateCheckSum().toString())
                .toList();

            String checksumString = String.join("", checksums);

            return MD5Util.computeMD5(checksumString);
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private Liquibase setupLiquibase(RunnerProperties runnerProps) throws FileNotFoundException {
        ResourceAccessor accessor = getResourceAccessor(runnerProps);
        Database database = null;

        Liquibase liquibase = new Liquibase(changeLogPath, accessor, database);
        setParameters(liquibase);

        return liquibase;
    }

    @Override
    public void run(RunnerProperties runnerProps, TargetDatasource targetDatasource) throws MojoExecutionException {
        setDefaultLocations(runnerProps);

        try (Liquibase liquibase = setupLiquibase(runnerProps, targetDatasource)) {
            liquibase.update();
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private Liquibase setupLiquibase(RunnerProperties runnerProps, TargetDatasource targetDatasource) throws SQLException, DatabaseException, FileNotFoundException {
        Driver driver = targetDatasource.getDriverInstance();

        Properties properties = new Properties();
        properties.put("user", targetDatasource.getUsername());
        properties.put("password", targetDatasource.getPassword());

        ResourceAccessor accessor = getResourceAccessor(runnerProps);

        Connection connection = driver.connect(targetDatasource.getUrl(), properties);
        Database database = createDatabase(connection);

        Liquibase liquibase = new Liquibase(changeLogPath, accessor, database);
        setParameters(liquibase);

        return liquibase;
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
