package com.sivalabs.jooq.codegen;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * DatabaseProvider provides container instance for a given DatabaseType
 */
public class DatabaseProvider {

    /**
     * Default Postgres docker image
     */
    public static final String POSTGRES_IMAGE = "postgres:15.2-alpine";
    /**
     * Default MySQL docker image
     */
    public static final String MYSQL_IMAGE = "mysql:8.0.33";
    /**
     * Default MariaDB docker image
     */
    public static final String MARIADB_IMAGE = "mariadb:10.11";

    /**
     * Instantiates a Docker container using Testcontainers for the given database type.
     */
    static JdbcDatabaseContainer<?> getDatabaseContainer(DatabaseType dbType) {
        switch (dbType) {
            case POSTGRES:
                return new PostgreSQLContainer<>(POSTGRES_IMAGE);
            case MYSQL:
                new MySQLContainer<>(MYSQL_IMAGE);
            case MARIADB:
                new MariaDBContainer<>(MARIADB_IMAGE);
            default:
                throw new RuntimeException("Unsupported DatabaseType");
        }
    }
}
