package assertions;

import static common.Common.GENERATED_SOURCES_PATH;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import org.assertj.core.api.AbstractAssert;

/**
 * Generated sources schema assert, it picks generated sources root and makes assertions based on existence generated files
 *
 * @see common.Common Common constants
 */
public class SchemaAssert extends AbstractAssert<SchemaAssert, Path> {

    private SchemaAssert(String s) {
        super(GENERATED_SOURCES_PATH.resolve(s), SchemaAssert.class);
    }

    private SchemaAssert() {
        super(GENERATED_SOURCES_PATH, SchemaAssert.class);
    }

    /**
     * Returns assert under generated sources with provided schema name directory
     *
     * @param name Schema name
     */
    public static SchemaAssert assertThatSchema(String name) {
        return new SchemaAssert(name);
    }

    /**
     * Returns assert under generated sources without schema name directory
     */
    public static SchemaAssert assertThatDefaultSchema() {
        return new SchemaAssert();
    }

    public SchemaAssert containsTable(String tableName) {
        var table = actual.resolve("tables").resolve(tableName);
        assertThat(table)
                .withFailMessage("%s directory does not contain file %s", actual, tableName)
                .isNotEmptyFile();
        return this;
    }
}
