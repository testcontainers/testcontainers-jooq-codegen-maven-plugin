package org.testcontainers.jooq.codegen.datasource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.Driver;
import java.util.Objects;
import javax.script.ScriptException;
import lombok.experimental.Delegate;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.shaded.org.apache.commons.io.FileUtils;
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils;

/**
 * Containerized target datasource
 */
public final class ContainerTargetDatasource implements TargetDatasource {
    private static final String FILESYSTEM_PREFIX = "filesystem:";
    private static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * Getting datasource properties from container, auto stopping container <br/>
     * {@link AutoCloseable} is implemented by container and {@code close()} delegated to {@code container.stop()}
     */
    @Delegate
    private final JdbcDatabaseContainer<?> container;

    public ContainerTargetDatasource(JdbcDatabaseContainer<?> container, String initScript) {
        this.container = Objects.requireNonNull(container);
        this.container.setWaitStrategy(new HostPortWaitStrategy());
        this.container.start();
        runInitScript(initScript);
    }

    @Override
    public String getUrl() {
        return container.getJdbcUrl();
    }

    @Override
    public Driver getDriverInstance() {
        return container.getJdbcDriverInstance();
    }

    private void runInitScript(String initScript) {
        if (StringUtils.isEmpty(initScript)) {
            return;
        }
        try (var jdbcDelegate = new JdbcDatabaseDelegate(container, "")) {
            if (initScript.startsWith(FILESYSTEM_PREFIX)) {
                var file = Path.of(initScript.substring(FILESYSTEM_PREFIX.length()))
                        .toAbsolutePath()
                        .toFile();
                try {
                    var scriptBody = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
                    ScriptUtils.executeDatabaseScript(jdbcDelegate, initScript, scriptBody);
                    return;
                } catch (IOException e) {
                    throw new RuntimeException("Failed to load " + file.getAbsolutePath(), e);
                } catch (ScriptException e) {
                    throw new RuntimeException("Failed to execute " + file.getAbsolutePath(), e);
                }
            }
            var scriptClassPath = initScript.startsWith(CLASSPATH_PREFIX)
                    ? initScript.substring(CLASSPATH_PREFIX.length())
                    : initScript;
            ScriptUtils.runInitScript(jdbcDelegate, scriptClassPath);
        }
    }
}
