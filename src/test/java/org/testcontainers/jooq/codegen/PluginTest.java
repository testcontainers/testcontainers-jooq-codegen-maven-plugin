package org.testcontainers.jooq.codegen;

import static assertions.MavenProjectAssert.assertThatProject;
import static org.codehaus.plexus.testing.PlexusExtension.getTestFile;

import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration test plugin under real pom files <br/>
 * Sources by default are generated under the same test pom directory <br/>
 * You can check them after test runs, nothing gets into final jar
 */
public class PluginTest {

    @Rule
    public MojoRule mojoRule = new MojoRule();

    @Test
    public void testPostgresFlyway() throws Exception {
        // given
        MavenProject mavenProject = getMavenProject("postgres-flyway");

        // when
        mojoRule.lookupConfiguredMojo(mavenProject, "generate").execute();

        // then
        assertThatProject(mavenProject)
                .hasGeneratedJooqTable("Users.java")
                .hasGeneratedJooqTable("FlywaySchemaHistory.java");
    }

    @Test
    public void testPostgresLiquibase() throws Exception {
        // given
        MavenProject mavenProject = getMavenProject("postgres-liquibase");

        // when
        mojoRule.lookupConfiguredMojo(mavenProject, "generate").execute();

        // then
        assertThatProject(mavenProject)
                .hasGeneratedJooqTable("custom", "Person.java")
                .hasGeneratedJooqTable("custom", "Users.java")
                .hasGeneratedJooqTable("public_", "Databasechangelog.java");
    }

    @Test
    public void testMysqlFlyway() throws Exception {
        // given
        MavenProject mavenProject = getMavenProject("mysql-flyway");

        // when
        mojoRule.lookupConfiguredMojo(mavenProject, "generate").execute();

        // then
        assertThatProject(mavenProject)
                .hasGeneratedJooqTable("test", "FlywaySchemaHistory.java")
                .hasGeneratedJooqTable("test", "Users.java");
    }

    @Test
    public void testMariadbLiquibase() throws Exception {
        // given
        MavenProject mavenProject = getMavenProject("mariadb-liquibase");

        // when
        mojoRule.lookupConfiguredMojo(mavenProject, "generate").execute();

        // then
        assertThatProject(mavenProject)
                .hasGeneratedJooqTable("test", "Users.java")
                .hasGeneratedJooqTable("test", "Databasechangelog.java");
    }

    private MavenProject getMavenProject(String dirName) throws Exception {
        var baseDir = getTestFile("src/test/resources/pom/%s".formatted(dirName));
        var mavenProject = mojoRule.readMavenProject(baseDir);
        mojoRule.getContainer().addComponent(mavenProject, MavenProject.class, "");
        return mavenProject;
    }
}
