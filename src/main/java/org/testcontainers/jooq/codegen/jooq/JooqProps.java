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
     * Basedir relative which generation happens <br/>
     * Optional
     */
    @Parameter
    private String baseDir;

    public JooqProps() {}

    public Jdbc getJdbc() {
        return jdbc;
    }

    public void setJdbc(Jdbc jdbc) {
        this.jdbc = jdbc;
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
