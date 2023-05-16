package org.testcontainers.jooq.codegen;

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
        JdbcDatabaseContainer<?> container;
        try {
            switch (dbType) {
                case POSTGRES:
                    if (isEmpty(image)) {
                        image = POSTGRES_IMAGE;
                    }
                    container = new PostgreSQLContainer<>(image);
                    break;
                case MARIADB:
                    if (isEmpty(image)) {
                        image = MARIADB_IMAGE;
                    }
                    container = new MariaDBContainer<>(image);
                    break;
                case MYSQL:
                    if (isEmpty(image)) {
                        image = MYSQL_IMAGE;
                    }
                    container = new MySQLContainer<>(image);
                    break;
                default:
                    throw new RuntimeException("Unsupported DatabaseType: " + dbType);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to instantiate database using Testcontainers", e);
        }
        if (isNotEmpty(props.getUsername())) {
            container.withUsername(props.getUsername());
        }
        if (isNotEmpty(props.getPassword())) {
            container.withPassword(props.getPassword());
        }
        if (isNotEmpty(props.getDatabaseName())) {
            container.withDatabaseName(props.getDatabaseName());
        }
        return container;
    }

    private static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    private static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
