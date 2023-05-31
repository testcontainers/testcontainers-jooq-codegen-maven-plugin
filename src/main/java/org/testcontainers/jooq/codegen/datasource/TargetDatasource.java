package org.testcontainers.jooq.codegen.datasource;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Predicate;
import org.jooq.meta.jaxb.Jdbc;
import org.testcontainers.jooq.codegen.database.DatabaseProps;
import org.testcontainers.jooq.codegen.database.DatabaseProvider;

/** Datasource to migrate and generate sources on */
public abstract class TargetDatasource implements AutoCloseable {

    public static TargetDatasource createOrJoinExisting(Jdbc jdbc, DatabaseProps database) {
        if (needSpinContainer(jdbc)) {
            var databaseContainer = DatabaseProvider.getDatabaseContainer(database);
            return new ContainerTargetDatasource(databaseContainer);
        }

        return new ExistingTargetDatasource(jdbc);
    }

    private static boolean needSpinContainer(Jdbc jdbc) {
        return ((Predicate<Jdbc>) Objects::isNull)
                .or(props -> props.getUrl() == null)
                .or(props -> props.getUser() == null)
                .or(props -> props.getPassword() == null)
                .test(jdbc);
    }

    public abstract String getUrl();

    public abstract String getUsername();

    public abstract String getPassword();

    public abstract Driver getDriver() throws SQLException;
}
