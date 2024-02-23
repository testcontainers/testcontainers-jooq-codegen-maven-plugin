package org.testcontainers.jooq.codegen.database;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Database configuration properties.
 */
@Data
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
    /**
     * Optional
     */
    @Parameter
    private String initScript;
}
