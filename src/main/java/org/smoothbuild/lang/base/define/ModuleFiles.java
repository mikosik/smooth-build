package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.util.io.Paths;

public record ModuleFiles(Space space, FileLocation smoothFile, Optional<FileLocation> nativeFile) {

  public String name() {
    return Paths.removeExtension(smoothFile.prefixedPath());
  }
}
