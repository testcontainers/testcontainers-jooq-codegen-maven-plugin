package org.testcontainers.jooq.codegen.datasource;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.jooq.meta.jaxb.Jdbc;

/** Datasource using provided parameters */
public final class ExistingTargetDatasource implements TargetDatasource {
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
        return jdbc.getUsername();
    }

    @Override
    public String getPassword() {
        return jdbc.getPassword();
    }

    @Override
    public Driver getDriver() throws SQLException {
        return DriverManager.getDriver(jdbc.getDriver());
    }

    @Override
    public void close() {}
}
