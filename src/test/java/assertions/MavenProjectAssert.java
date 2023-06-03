package assertions;

import static common.Common.DEFAULT_GENERATED_BASEDIR;
import static common.Common.DEFAULT_GENERATED_PACKAGE;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.AbstractAssert;

/**
 * Maven project assert, used for testing generated sources or rest project specific thongs
 */
public class MavenProjectAssert extends AbstractAssert<MavenProjectAssert, MavenProject> {

    private final Path generatedSourcesPackage;

    protected MavenProjectAssert(MavenProject mavenProject, Path generatedBasedir, Path generatedPackage) {
        super(mavenProject, MavenProjectAssert.class);
        generatedSourcesPackage =
                mavenProject.getBasedir().toPath().resolve(generatedBasedir).resolve(generatedPackage);
    }

    public static MavenProjectAssert assertThatProject(MavenProject project) {
        return new MavenProjectAssert(project, DEFAULT_GENERATED_BASEDIR, DEFAULT_GENERATED_PACKAGE);
    }

    public static MavenProjectAssert assertThatProject(MavenProject project, Path baseDir, Path pkg) {
        return new MavenProjectAssert(project, baseDir, pkg);
    }

    public MavenProjectAssert hasGeneratedJooqTable(String schema, String tableFile) {
        var schemaDir = generatedSourcesPackage.resolve(schema);
        var tablesDir = schemaDir.resolve("tables");
        var table = tablesDir.resolve(tableFile);
        assertThat(table)
                .withFailMessage("Maven project generated sources %s does not contain file: %s", tablesDir, tableFile)
                .isNotEmptyFile();
        return this;
    }

    public MavenProjectAssert hasGeneratedJooqTable(String tableFile) {
        var tableDir = generatedSourcesPackage.resolve("tables");
        var file = tableDir.resolve(tableFile);
        assertThat(file)
                .withFailMessage("Maven project generated sources does not contain file: %s", file)
                .isNotEmptyFile();
        return this;
    }
}
