package org.testcontainers.jooq.codegen.database;

import java.util.Optional;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.mariadb.MariaDBContainer;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/** DatabaseProvider provides container instance for a given DatabaseType */
public class DatabaseProvider {

    /** Instantiates a Docker container using Testcontainers for the given database type. */
    public static JdbcDatabaseContainer<?> getDatabaseContainer(DatabaseProps props) {
        DatabaseType dbType = props.getType();
        String image = Optional.ofNullable(props.getContainerImage()).orElse(dbType.getDefaultImage());
        JdbcDatabaseContainer<?> container =
                switch (dbType) {
                    case POSTGRES ->
                        new PostgreSQLContainer(DockerImageName.parse(image).asCompatibleSubstituteFor("postgres"));
                    case MARIADB ->
                        new MariaDBContainer(DockerImageName.parse(image).asCompatibleSubstituteFor("mariadb"));
                    case MYSQL ->
                        new MySQLContainer(DockerImageName.parse(image).asCompatibleSubstituteFor("mysql"));
                };
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
