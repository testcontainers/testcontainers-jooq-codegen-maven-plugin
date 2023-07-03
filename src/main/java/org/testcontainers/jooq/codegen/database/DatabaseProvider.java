package org.testcontainers.jooq.codegen.database;

import java.util.Optional;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/** DatabaseProvider provides container instance for a given DatabaseType */
public class DatabaseProvider {

    /** Instantiates a Docker container using Testcontainers for the given database type. */
    public static JdbcDatabaseContainer<?> getDatabaseContainer(DatabaseProps props) {
        DatabaseType dbType = props.getType();
        String image = Optional.ofNullable(props.getContainerImage()).orElse(dbType.getDefaultImage());
        JdbcDatabaseContainer<?> container;
        switch (dbType) {
            case POSTGRES:
                container = new PostgreSQLContainer<>(image);
                break;
            case MARIADB:
                container = new MariaDBContainer<>(image);
                break;
            case MYSQL:
                container = new MySQLContainer<>(image);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown DatabaseType: %s.", dbType));
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
