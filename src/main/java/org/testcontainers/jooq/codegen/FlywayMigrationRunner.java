package org.testcontainers.jooq.codegen;

import java.util.Map;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;

public class FlywayMigrationRunner {
    private final Map<String, String> flywayProps;

    public FlywayMigrationRunner(Map<String, String> flywayProps) {
        this.flywayProps = flywayProps;
    }

    void run() {
        FluentConfiguration configuration = new FluentConfiguration();
        configuration.configuration(flywayProps);
        Flyway flyway = configuration.load();
        flyway.migrate();
    }
}
