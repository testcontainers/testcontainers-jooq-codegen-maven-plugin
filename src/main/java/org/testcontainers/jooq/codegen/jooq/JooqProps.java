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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JooqProps jooqProps = (JooqProps) o;
        return java.util.Objects.equals(jdbc, jooqProps.jdbc)
                && java.util.Objects.equals(generator, jooqProps.generator)
                && java.util.Objects.equals(baseDir, jooqProps.baseDir);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(jdbc, generator, baseDir);
    }

    @Override
    public String toString() {
        return "JooqProps{" + "jdbc=" + jdbc + ", generator=" + generator + ", baseDir='" + baseDir + '\'' + '}';
    }
}
