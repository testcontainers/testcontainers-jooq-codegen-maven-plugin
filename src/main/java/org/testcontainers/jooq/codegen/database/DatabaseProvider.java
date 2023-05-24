package org.testcontainers.jooq.codegen.database;

import static org.testcontainers.jooq.codegen.database.DatabaseType.*;

import java.util.Optional;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * DatabaseProvider provides container instance for a given DatabaseType
 */
public class DatabaseProvider {

    /**
     * Instantiates a Docker container using Testcontainers for the given database type.
     */
    public static JdbcDatabaseContainer<?> getDatabaseContainer(DatabaseProps props) {
        DatabaseType dbType = props.getType();
        Optional<String> image = Optional.ofNullable(props.getContainerImage());
        JdbcDatabaseContainer<?> container;
        try {
            switch (dbType) {
                case POSTGRES -> container = new PostgreSQLContainer<>(image.orElse(POSTGRES.getDefaultImage()));
                case MARIADB -> container = new MariaDBContainer<>(image.orElse(MARIADB.getDefaultImage()));
                case MYSQL -> container = new MySQLContainer<>(image.orElse(MYSQL.getDefaultImage()));
                default -> throw new IllegalArgumentException("Unsupported DatabaseType: " + dbType);
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
