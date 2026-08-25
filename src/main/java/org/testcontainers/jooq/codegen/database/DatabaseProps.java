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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseProps that = (DatabaseProps) o;
        return java.util.Objects.equals(type, that.type)
                && java.util.Objects.equals(containerImage, that.containerImage)
                && java.util.Objects.equals(username, that.username)
                && java.util.Objects.equals(password, that.password)
                && java.util.Objects.equals(databaseName, that.databaseName);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(type, containerImage, username, password, databaseName);
    }

    @Override
    public String toString() {
        return "DatabaseProps{" + "type="
                + type + ", containerImage='"
                + containerImage + '\'' + ", username='"
                + username + '\'' + ", password='"
                + password + '\'' + ", databaseName='"
                + databaseName + '\'' + '}';
    }
}
