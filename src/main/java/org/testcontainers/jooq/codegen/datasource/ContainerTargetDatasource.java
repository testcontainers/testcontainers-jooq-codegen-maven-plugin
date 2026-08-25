package org.testcontainers.jooq.codegen.datasource;

import java.sql.Driver;
import java.util.Objects;
import org.testcontainers.containers.JdbcDatabaseContainer;

/**
 * Containerized target datasource
 */
public final class ContainerTargetDatasource implements TargetDatasource {
    /**
     * Getting datasource properties from container, auto stopping container <br/>
     * {@link AutoCloseable} is implemented by container and {@code close()} delegated to {@code container.stop()}
     */
    private final JdbcDatabaseContainer<?> container;

    public ContainerTargetDatasource(JdbcDatabaseContainer<?> container) {
        this.container = Objects.requireNonNull(container);
        this.container.start();
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
    public Driver getDriverInstance() {
        return container.getJdbcDriverInstance();
    }

    @Override
    public void close() throws Exception {
        container.close();
    }
}
