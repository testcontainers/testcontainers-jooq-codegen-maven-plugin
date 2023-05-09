package com.sivalabs.jooq.codegen;

/**
 * Plugin configuration properties.
 */
public class PluginProps {
    private DatabaseProps database;
    private FlywayProps flyway;

    public DatabaseProps getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseProps database) {
        this.database = database;
    }

    public FlywayProps getFlyway() {
        return flyway;
    }

    public void setFlyway(FlywayProps flyway) {
        this.flyway = flyway;
    }
}
