package org.testcontainers.jooq.codegen.jooq;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;

/**
 * Jooq specific properties
 */
@Data
public class JooqProps {

    /**
     * Jdbc specific properties <br/>
     * Optional
     */
    @Parameter
    private Jdbc jdbc;

    /**
     * Sources generator specific properties <br/>
     * Optional
     */
    @Parameter
    private Generator generator;

    /**
     * Basedir relative which generation happens <br/>
     * Optional
     */
    @Parameter
    private String baseDir;
}
