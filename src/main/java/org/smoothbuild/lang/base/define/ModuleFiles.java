package org.smoothbuild.lang.base.define;

import java.util.Optional;

public record ModuleFiles(
    ModulePath path, FileLocation smoothFile, Optional<FileLocation> nativeFile) {
}
