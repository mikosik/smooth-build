package org.smoothbuild.lang.base.define;

import java.util.Optional;

public record ModuleFiles(FileLocation smoothFile, Optional<FileLocation> nativeFile) {
}
