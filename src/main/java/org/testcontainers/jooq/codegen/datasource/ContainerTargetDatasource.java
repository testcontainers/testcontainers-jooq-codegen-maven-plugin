package org.testcontainers.jooq.codegen.datasource;

import java.sql.Driver;
import java.util.Objects;
import lombok.experimental.Delegate;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;

/**
 * Containerized target datasource
 */
public final class ContainerTargetDatasource implements TargetDatasource {
    /**
     * Getting datasource properties from container, auto stopping container <br/>
     * {@link AutoCloseable} is implemented by container and {@code close()} delegated to {@code container.stop()}
     */
    @Delegate
    private final JdbcDatabaseContainer<?> container;

    public ContainerTargetDatasource(JdbcDatabaseContainer<?> container) {
        this.container = Objects.requireNonNull(container);
        this.container.setWaitStrategy(new HostPortWaitStrategy());
        this.container.start();
    }

    @Override
    public String getUrl() {
        return container.getJdbcUrl();
    }

    @Override
    public Driver getDriverInstance() {
        return container.getJdbcDriverInstance();
    }
}
