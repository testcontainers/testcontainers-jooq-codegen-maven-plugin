package org.testcontainers.jooq.codegen.datasource;

import java.sql.Driver;
import java.sql.DriverManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;
import org.jooq.meta.jaxb.Jdbc;

/**
 * Datasource using provided parameters
 */
@RequiredArgsConstructor
public final class ExistingTargetDatasource implements TargetDatasource {

    /**
     * Gets datasource properties from provided jdbc connection configuration
     */
    @Delegate
    private final Jdbc jdbc;

    @Override
    @SneakyThrows
    public Driver getDriverInstance() {
        return DriverManager.getDriver(jdbc.getDriver());
    }

    @Override
    public void close() {}
}
