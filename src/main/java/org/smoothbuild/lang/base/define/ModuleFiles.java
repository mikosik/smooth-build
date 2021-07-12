package org.smoothbuild.lang.base.define;

import java.util.Optional;

import org.smoothbuild.io.fs.space.FilePath;

import com.google.common.collect.ImmutableList;

public record ModuleFiles(FilePath smoothFile, Optional<FilePath> nativeFile) {
  public ImmutableList<FilePath> asList() {
    if (nativeFile.isPresent()) {
      return ImmutableList.of(smoothFile, nativeFile.get());
    } else {
      return ImmutableList.of(smoothFile);
    }
  }
}
