package com.sivalabs.jooq.codegen;

import java.util.Properties;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

/**
 * FlywayMigrationRunner executes the flyway migration scripts based on the given configuration properties.
 */
public class FlywayMigrationRunner {
    private final FlywayProps flywayProps;

    public FlywayMigrationRunner(FlywayProps flywayProps) {
        this.flywayProps = flywayProps;
    }

    void run() {
        FluentConfiguration configuration = new FluentConfiguration();
        Properties properties = this.getFlywayConfig();
        configuration.configuration(properties);
        Flyway flyway = configuration.load();
        flyway.migrate();
    }

    private Properties getFlywayConfig() {
        Properties properties = new Properties();
        properties.put("flyway.url", flywayProps.getJdbcUrl());
        properties.put("flyway.user", flywayProps.getUsername());
        properties.put("flyway.password", flywayProps.getPassword());
        properties.put("flyway.locations", flywayProps.getLocations());

        return properties;
    }
}
