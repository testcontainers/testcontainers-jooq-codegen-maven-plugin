package org.testcontainers.jooq.codegen.util;

import java.util.Optional;

public class OptionalUtils {
    public static boolean bothPresent(Optional<?> first, Optional<?> second) {
        return first.isPresent() && second.isPresent();
    }
}
