package org.testcontainers.jooq.codegen.datasource;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jooq.meta.jaxb.Jdbc;

/**
 * Datasource using provided parameters
 */
public final class ExistingTargetDatasource implements TargetDatasource {

    /**
     * Gets datasource properties from provided jdbc connection configuration
     */
    private final Jdbc jdbc;

    public ExistingTargetDatasource(Jdbc jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String getUrl() {
        return jdbc.getUrl();
    }

    @Override
    public String getUsername() {
        return jdbc.getUser();
    }

    @Override
    public String getPassword() {
        return jdbc.getPassword();
    }

    @Override
    public Driver getDriverInstance() {
        try {
            return DriverManager.getDriver(jdbc.getDriver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {}
}
