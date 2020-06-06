package org.smoothbuild.lang.base;

import java.nio.file.Path;

public record ShortablePath(Path path, String shorted) {
}
