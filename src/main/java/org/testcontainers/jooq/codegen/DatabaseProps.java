package org.testcontainers.jooq.codegen;

/**
 * Database configuration properties.
 */
public class DatabaseProps {
    private DatabaseType type;
    private String containerImage;

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
}
