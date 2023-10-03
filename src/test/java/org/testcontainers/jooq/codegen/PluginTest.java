package org.testcontainers.jooq.codegen;

import static org.codehaus.plexus.PlexusTestCase.getTestFile;
import static org.testcontainers.jooq.codegen.assertions.MavenProjectAssert.assertThatProject;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Integration test plugin under real pom files <br/>
 * Sources by default are generated under the same test pom directory <br/>
 * You can check them after test runs, nothing gets into final jar
 */
public class PluginTest {
    private static final String userHome = System.getProperty("user.home");

    @AfterClass
    public static void afterClass() {
        System.setProperty("user.home", userHome);
    }

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
    public void testPostgissFlyway() throws Exception {
        // given
        MavenProject mavenProject = getMavenProject("postgis-flyway");

        // when
        mojoRule.lookupConfiguredMojo(mavenProject, "generate").execute();

        // then
        assertThatProject(mavenProject)
                .hasGeneratedJooqTable("Users.java")
                .hasGeneratedJooqTable("FlywaySchemaHistory.java");
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

    /**
     * Ensures that default <b><i>flyway.conf</i></b> has been loaded and default locations picked up for flyway
     */
    @Test
    public void testMysqlFlywayDefaultConfig() throws Exception {
        // given
        MavenProject mavenProject = getMavenProject("mysql-flyway-with-flyway-config-file");

        // when
        Mojo generate = mojoRule.lookupConfiguredMojo(mavenProject, "generate");
        generate.execute();

        // then
        assertThatProject(mavenProject)
                .hasGeneratedJooqTable("test", "ConfigOverriddenHistoryTable.java")
                .hasGeneratedJooqTable("test", "Users.java");
    }

    private MavenProject getMavenProject(String dirName) throws Exception {
        var baseDir = getTestFile("src/test/resources/pom/%s".formatted(dirName));
        var mavenProject = mojoRule.readMavenProject(baseDir);
        mojoRule.getContainer().addComponent(mavenProject, MavenProject.class, "");
        setUserHomeToCurrentProject(mavenProject);
        return mavenProject;
    }

    /**
     * It makes current user home directory to maven pom basedir, allowing to apply relative configs and migraitons
     */
    private void setUserHomeToCurrentProject(MavenProject mavenProject) {
        System.setProperty("user.home", mavenProject.getBasedir().getAbsolutePath());
    }
}
