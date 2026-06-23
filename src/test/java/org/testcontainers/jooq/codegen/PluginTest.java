package org.testcontainers.jooq.codegen;

import static org.apache.commons.io.FileUtils.copyDirectory;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.testcontainers.jooq.codegen.assertions.GeneratedSourcesAssert.assertThatProject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.maven.shared.verifier.VerificationException;
import org.apache.maven.shared.verifier.Verifier;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Integration test plugin under real pom files
 */
class PluginTest {
    private @TempDir Path tempDirectory;

    static Stream<Arguments> testArguments() {
        return Stream.of(
                arguments(
                        "postgres-flyway",
                        "src/test/resources/pom/postgres-flyway",
                        Set.of("Users.java", "FlywaySchemaHistory.java"),
                        Map.of()),
                arguments(
                        "postgres-liquibase",
                        "src/test/resources/pom/postgres-liquibase",
                        Set.of(),
                        Map.of(
                                "custom",
                                List.of("Person.java", "Users.java"),
                                "public_",
                                List.of("Databasechangelog.java"))),
                arguments(
                        "postgis-flyway",
                        "src/test/resources/pom/postgis-flyway",
                        Set.of("Users.java", "FlywaySchemaHistory.java"),
                        Map.of()),
                arguments(
                        "mysql-flyway",
                        "src/test/resources/pom/mysql-flyway",
                        Set.of(),
                        Map.of("test", List.of("Users.java", "FlywaySchemaHistory.java"))),
                arguments(
                        "mariadb-liquibase",
                        "src/test/resources/pom/mariadb-liquibase",
                        Set.of(),
                        Map.of("test", List.of("Users.java", "Databasechangelog.java"))),
                arguments(
                        "mysql-flyway-with-flyway-config-file",
                        "src/test/resources/pom/mysql-flyway-with-flyway-config-file",
                        Set.of(),
                        Map.of("test", List.of("Users.java", "ConfigOverriddenHistoryTable.java"))));
    }

    @MethodSource("testArguments")
    @ParameterizedTest(name = "{0}")
    void testJooqCodeGeneration(
            String testCaseName,
            String projectDirectory,
            Set<String> expectedTables,
            Map<String, List<String>> expectedSchemaToTables)
            throws IOException, VerificationException {
        copyDirectory(Path.of(projectDirectory).toFile(), tempDirectory.toFile());

        var verifier = new Verifier(tempDirectory.toAbsolutePath().toString());
        verifier.addCliArgument("generate-sources");
        verifier.execute();
        verifier.verifyErrorFreeLog();

        var assertion = assertThatProject(tempDirectory);
        expectedTables.forEach(assertion::hasGeneratedJooqTable);
        expectedSchemaToTables.forEach(
                (schema, tables) -> tables.forEach(table -> assertion.hasGeneratedJooqTable(schema, table)));
    }
}
