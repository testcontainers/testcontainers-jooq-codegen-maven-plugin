package org.testcontainers.jooq.codegen.database;

/** Database Types supported by the plugin */
public enum DatabaseType {
    POSTGRES("postgres:18-alpine"),
    MYSQL("mysql:9.6.0"),
    MARIADB("mariadb:12");

    private final String defaultImage;

    DatabaseType(String defaultImage) {
        this.defaultImage = defaultImage;
    }

    public String getDefaultImage() {
        return defaultImage;
    }
}
