package com.sivalabs.jooq.codegen;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class DatabaseProvider {

    public static final String POSTGRES_IMAGE = "postgres:15.2-alpine";
    public static final String MYSQL_IMAGE = "mysql:8.0.33";
    public static final String MARIADB_IMAGE = "mariadb:10.11";

    static JdbcDatabaseContainer<?> getDatabaseContainer(DatabaseType dbType) {
        switch (dbType) {
            case POSTGRES: return new PostgreSQLContainer<>(POSTGRES_IMAGE);
            case MYSQL: new MySQLContainer<>(MYSQL_IMAGE);
            case MARIADB: new MariaDBContainer<>(MARIADB_IMAGE);
            default: throw new RuntimeException("Unsupported DatabaseType");
        }
    }
}
