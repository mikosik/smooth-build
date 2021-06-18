package org.smoothbuild.lang.base.define;

import java.util.Optional;

public record ModuleFiles(FilePath smoothFile, Optional<FilePath> nativeFile) {
}
