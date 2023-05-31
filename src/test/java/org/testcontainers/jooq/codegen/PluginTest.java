package org.testcontainers.jooq.codegen;

import static assertions.SchemaAssert.assertThatDefaultSchema;
import static assertions.SchemaAssert.assertThatSchema;
import static common.Common.GENERATED_ROOT_DIR;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

/**
 * Integration test plugin under real pom files
 */
public class PluginTest extends AbstractMojoTestCase {

    /**
     * Cleanup generated sources
     */
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        FileUtils.deleteDirectory(GENERATED_ROOT_DIR);
    }

    public void testPostgresFlyway() throws Exception {
        // given
        File pom = getTestPom("postgres-flyway-pom.xml");

        // when
        lookupMojo("generate", pom).execute();

        // then
        assertThatDefaultSchema().containsTable("Users.java");
        assertThatDefaultSchema().containsTable("FlywaySchemaHistory.java");
    }

    public void testPostgresLiquibase() throws Exception {
        // given
        File pom = getTestPom("postgres-liquibase-pom.xml");

        // when
        lookupMojo("generate", pom).execute();

        // then
        assertThatSchema("custom").containsTable("Person.java");
        assertThatSchema("custom").containsTable("Users.java");
        assertThatSchema("public_").containsTable("Databasechangelog.java");
    }

    public void testMysqlFlyway() throws Exception {
        // given
        File pom = getTestPom("mysql-flyway-pom.xml");

        // when
        lookupMojo("generate", pom).execute();

        // then
        assertThatSchema("test").containsTable("Users.java");
        assertThatSchema("test").containsTable("FlywaySchemaHistory.java");
    }

    public void testMariadbLiquibase() throws Exception {
        // given
        File pom = getTestPom("mariadb-liquibase-pom.xml");

        // when
        lookupMojo("generate", pom).execute();

        // then
        assertThatSchema("test").containsTable("Users.java");
        assertThatSchema("test").containsTable("Databasechangelog.java");
    }

    private File getTestPom(String filename) {
        return getTestFile("src/test/resources/pom/%s".formatted(filename));
    }
}
