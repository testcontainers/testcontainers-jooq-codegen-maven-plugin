package com.sivalabs.jooq.codegen;

/**
 * Plugin configuration properties.
 */
public class PluginProps {
    private DatabaseType dbType;
    private FlywayProps flyway;

    public DatabaseType getDbType() {
        return dbType;
    }

    public void setDbType(DatabaseType dbType) {
        this.dbType = dbType;
    }

    public FlywayProps getFlyway() {
        return flyway;
    }

    public void setFlyway(FlywayProps flyway) {
        this.flyway = flyway;
    }
}
