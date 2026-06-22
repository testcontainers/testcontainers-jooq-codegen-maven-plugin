package org.testcontainers.jooq.codegen.assertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.jooq.codegen.common.Common.DEFAULT_GENERATED_BASEDIR;
import static org.testcontainers.jooq.codegen.common.Common.DEFAULT_GENERATED_PACKAGE;

import java.nio.file.Path;
import org.assertj.core.api.AbstractAssert;

/**
 * Assertions for generated sources produced by the plugin.
 */
public class GeneratedSourcesAssert extends AbstractAssert<GeneratedSourcesAssert, Path> {
    private final Path generatedSourcesPackage;

    protected GeneratedSourcesAssert(Path projectDir, Path generatedBasedir, Path generatedPackage) {
        super(projectDir, GeneratedSourcesAssert.class);
        this.generatedSourcesPackage = projectDir.resolve(generatedBasedir).resolve(generatedPackage);
    }

    public static GeneratedSourcesAssert assertThatProject(Path projectDir) {
        return new GeneratedSourcesAssert(projectDir, DEFAULT_GENERATED_BASEDIR, DEFAULT_GENERATED_PACKAGE);
    }

    public static GeneratedSourcesAssert assertThatProject(
            Path projectDir, Path generatedBasedir, Path generatedPackage) {
        return new GeneratedSourcesAssert(projectDir, generatedBasedir, generatedPackage);
    }

    public GeneratedSourcesAssert hasGeneratedJooqTable(String schema, String tableFile) {
        Path file = generatedSourcesPackage.resolve(schema).resolve("tables").resolve(tableFile);
        assertThat(file)
                .withFailMessage("Generated sources do not contain file: %s", file)
                .isRegularFile()
                .isNotEmptyFile();
        return this;
    }

    public GeneratedSourcesAssert hasGeneratedJooqTable(String tableFile) {
        Path file = generatedSourcesPackage.resolve("tables").resolve(tableFile);
        assertThat(file)
                .withFailMessage("Generated sources do not contain file: %s", file)
                .isRegularFile()
                .isNotEmptyFile();
        return this;
    }
}
