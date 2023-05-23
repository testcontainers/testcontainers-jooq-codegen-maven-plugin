package org.testcontainers.jooq.codegen.migration.runner;

import java.util.HashMap;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.internal.configuration.ConfigUtils;

/**
 * Map with flyway properties, each added property will be prefixed with "flyway"
 */
public class FlywayRunner extends HashMap<String, String> implements MigrationRunner {

    @Override
    public String put(String key, String value) {
        var prefixKey = addFlywayPrefix(key);
        return super.put(prefixKey, value);
    }

    @Override
    public void run(RunnerProperties runnerProperties) {
        put(ConfigUtils.URL, runnerProperties.jdbcUrl());
        put(ConfigUtils.USER, runnerProperties.username());
        put(ConfigUtils.PASSWORD, runnerProperties.password());

        FluentConfiguration configuration = new FluentConfiguration();
        configuration.configuration(this);

        Flyway flyway = configuration.load();
        flyway.migrate();
    }

    private String addFlywayPrefix(String key) {
        return key.startsWith("flyway.") ? key : "flyway." + key;
    }
}
