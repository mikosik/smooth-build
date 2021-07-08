package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.io.fs.space.FilePath;

public record ModuleFiles(FilePath smoothFile, Optional<FilePath> nativeFile) {
}
