package org.testcontainers.jooq.codegen.migration.runner;

import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.internal.configuration.ConfigUtils;
import org.testcontainers.jooq.codegen.datasource.TargetDatasource;

import java.util.HashMap;
import java.util.UUID;

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
    public String checksum(RunnerProperties runnerProperties) {
        // Checksums have not been implemented for Flyway yet
        return UUID.randomUUID().toString();
    }

    @Override
    public void run(RunnerProperties runnerProperties, TargetDatasource targetDatasource) {
        var log = runnerProperties.log();
        put(ConfigUtils.URL, targetDatasource.getUrl());
        put(ConfigUtils.USER, targetDatasource.getUsername());
        put(ConfigUtils.PASSWORD, targetDatasource.getPassword());

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
        var defaultResourceLocation = "%s%s/src/main/resources/db/migration"
                .formatted(
                        Location.FILESYSTEM_PREFIX,
                        runnerProperties.mavenProject().getBasedir().getAbsolutePath());
        var defaultClasspathLocation = "%s%s".formatted("classpath:", "/db/migration");
        putIfAbsent(ConfigUtils.LOCATIONS, "%s,%s".formatted(defaultResourceLocation, defaultClasspathLocation));
    }

    private String addFlywayPrefix(String key) {
        return key.startsWith("flyway.") ? key : "flyway." + key;
    }
}
