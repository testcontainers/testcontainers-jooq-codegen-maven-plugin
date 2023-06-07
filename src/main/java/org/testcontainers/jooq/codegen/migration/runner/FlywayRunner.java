package org.testcontainers.jooq.codegen.migration.runner;

import java.util.HashMap;
import org.flywaydb.core.api.Location;
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
        var log = runnerProperties.log();
        put(ConfigUtils.URL, runnerProperties.getUrl());
        put(ConfigUtils.USER, runnerProperties.getUsername());
        put(ConfigUtils.PASSWORD, runnerProperties.getPassword());

        var configuration = new FluentConfiguration().loadDefaultConfigurationFiles();

        addDefaults(runnerProperties);
        configuration.configuration(this);

        var flyway = configuration.load();
        var result = flyway.migrate();
        var message = result.migrationsExecuted > 0
                ? "Applied %s flyway migrations".formatted(result.migrationsExecuted)
                : "No flyway migrations were applied";
        log.info(message);
    }

    private void addDefaults(RunnerProperties runnerProperties) {
        putIfAbsent(
                ConfigUtils.LOCATIONS,
                "%s%s/src/main/resources/db/migration"
                        .formatted(
                                Location.FILESYSTEM_PREFIX,
                                runnerProperties.mavenProject().getBasedir().getAbsolutePath()));
    }

    private String addFlywayPrefix(String key) {
        return key.startsWith("flyway.") ? key : "flyway." + key;
    }
}
