package org.testcontainers.jooq.codegen;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class PluginTest extends AbstractMojoTestCase {

    private final Path generatedSourcesRoot = Path.of("target/generated-sources/jooq");
    private final Path generatedSourcesPath = generatedSourcesRoot.resolve("org/jooq/codegen/maven/test");
    private final Path tablesDir = generatedSourcesPath.resolve("tables");
    private final File generatedRootDir = generatedSourcesRoot.toFile();

    public void testPostgresFlyway() throws Exception {
        // given
        File pom = getTestFile("src/test/resources/pom/pom.xml");

        // when
        Plugin myMojo = (Plugin) lookupMojo("generate", pom);
        myMojo.execute();

        // then
        assertThat(tablesDir).isNotEmptyDirectory();
        assertThat(tablesDir.resolve("Users.java")).isNotEmptyFile();
        assertThat(generatedSourcesPath.resolve("Public.java")).isNotEmptyFile();

        // clean
        FileUtils.deleteDirectory(generatedRootDir);
    }
}
