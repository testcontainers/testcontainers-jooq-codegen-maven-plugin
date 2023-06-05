package org.testcontainers.jooq.codegen.datasource;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Predicate;
import org.jooq.meta.jaxb.Jdbc;
import org.testcontainers.jooq.codegen.database.DatabaseProps;
import org.testcontainers.jooq.codegen.database.DatabaseProvider;
import org.testcontainers.jooq.codegen.jooq.JooqProps;

/**
 * Datasource to migrate and generate sources on
 */
public interface TargetDatasource extends AutoCloseable {

    static TargetDatasource createOrJoinExisting(JooqProps jooq, DatabaseProps database) {
        if (needSpinContainer(jooq)) {
            var databaseContainer = DatabaseProvider.getDatabaseContainer(database);
            return new ContainerTargetDatasource(databaseContainer);
        }

        return new ExistingTargetDatasource(jooq.getJdbc());
    }

    private static boolean needSpinContainer(JooqProps jooq) {
        final var jdbc = jooq.getJdbc();
        return ((Predicate<Jdbc>) Objects::isNull)
                .or(props -> props.getUrl() == null)
                .or(props -> props.getUser() == null)
                .or(props -> props.getPassword() == null)
                .test(jdbc);
    }

    String getUrl();

    String getUsername();

    String getPassword();

    Driver getDriver() throws SQLException;
}
