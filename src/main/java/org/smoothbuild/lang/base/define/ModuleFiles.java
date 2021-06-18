package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.io.fs.base.FilePath;

public record ModuleFiles(FilePath smoothFile, Optional<FilePath> nativeFile) {
}
