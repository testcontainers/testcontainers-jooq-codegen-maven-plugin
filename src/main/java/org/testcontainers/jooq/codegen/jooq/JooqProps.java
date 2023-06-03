package org.testcontainers.jooq.codegen.jooq;

import org.apache.maven.plugins.annotations.Parameter;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;

/**
 * Jooq specific properties
 */
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
     * Skip jooq sources generation <br/>
     * Optional, default - false
     */
    @Parameter
    private boolean skip;

    /**
     * Basedir relative which generation happens <br/>
     * Optional
     */
    @Parameter
    private String baseDir;

    public Jdbc getJdbc() {
        return jdbc;
    }

    public Generator getGenerator() {
        return generator;
    }

    public boolean skip() {
        return skip;
    }

    public String getBaseDir() {
        return baseDir;
    }
}
