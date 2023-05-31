package org.testcontainers.jooq.codegen.datasource;

import java.sql.Driver;
import java.util.Objects;
import org.testcontainers.containers.JdbcDatabaseContainer;

/** Containerized target datasource */
public final class ContainerTargetDatasource extends TargetDatasource {
    private final JdbcDatabaseContainer<?> container;

    public ContainerTargetDatasource(JdbcDatabaseContainer<?> container) {
        this.container = Objects.requireNonNull(container);
    }

    @Override
    public String getUrl() {
        return container.getJdbcUrl();
    }

    @Override
    public String getUsername() {
        return container.getUsername();
    }

    @Override
    public String getPassword() {
        return container.getPassword();
    }

    @Override
    public Driver getDriver() {
        return container.getJdbcDriverInstance();
    }

    @Override
    public void close() throws Exception {
        container.stop();
    }
}
