package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.common.collect.Lists.list;

import java.util.Optional;

import org.smoothbuild.fs.space.FilePath;

import com.google.common.collect.ImmutableList;

public record ModuleResources(FilePath smoothFile, Optional<FilePath> nativeFile) {
  public ImmutableList<FilePath> asList() {
    if (nativeFile.isPresent()) {
      return list(smoothFile, nativeFile.get());
    } else {
      return list(smoothFile);
    }
  }
}
