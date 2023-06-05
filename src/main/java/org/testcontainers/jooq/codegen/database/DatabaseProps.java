package org.testcontainers.jooq.codegen.database;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * Database configuration properties.
 */
public class DatabaseProps {
    /**
     * Required
     */
    @Parameter(required = true)
    private DatabaseType type;

    /**
     * Optional
     */
    @Parameter
    private String containerImage;
    /**
     * Optional
     */
    @Parameter
    private String username;
    /**
     * Optional
     */
    @Parameter
    private String password;
    /**
     * Optional
     */
    @Parameter
    private String databaseName;

    public DatabaseType getType() {
        return type;
    }

    public void setType(DatabaseType type) {
        this.type = type;
    }

    public String getContainerImage() {
        return containerImage;
    }

    public void setContainerImage(String containerImage) {
        this.containerImage = containerImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
