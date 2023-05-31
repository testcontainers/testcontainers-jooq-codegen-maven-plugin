package common;

import java.io.File;
import java.nio.file.Path;

/**
 * Common constants for tests
 */
public class Common {
    public static final Path GENERATED_SOURCES_ROOT = Path.of("target/generated-sources/jooq");
    public static final Path GENERATED_SOURCES_PATH = GENERATED_SOURCES_ROOT.resolve("org/jooq/codegen/maven/test");
    public static final File GENERATED_ROOT_DIR = GENERATED_SOURCES_ROOT.toFile();
}
