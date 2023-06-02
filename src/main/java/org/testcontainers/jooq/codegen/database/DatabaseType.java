package org.testcontainers.jooq.codegen.database;

/** Database Types supported by the plugin */
public enum DatabaseType {
    POSTGRES("postgres:15.2-alpine"),
    MYSQL("mysql:8.0.33"),
    MARIADB("mariadb:10.11");

    private final String defaultImage;

    DatabaseType(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public String getDefaultImage() {
        return defaultImage;
    }
}
