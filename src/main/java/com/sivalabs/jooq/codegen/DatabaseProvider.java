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
    static JdbcDatabaseContainer<?> getDatabaseContainer(DatabaseProps props) {
        DatabaseType dbType = props.getType();
        String image = props.getContainerImage();
        try {
            switch (dbType) {
                case POSTGRES:
                    if (image == null) {
                        image = POSTGRES_IMAGE;
                    }
                    return new PostgreSQLContainer<>(image);
                case MARIADB:
                    if (image == null) {
                        image = MARIADB_IMAGE;
                    }
                    return new MariaDBContainer<>(image);
                case MYSQL:
                    if (image == null) {
                        image = MYSQL_IMAGE;
                    }
                    return new MySQLContainer<>(image);
                default:
                    throw new RuntimeException("Unsupported DatabaseType: " + dbType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to instantiate database using Testcontainers", e);
        }
    }
}
