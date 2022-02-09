package org.smoothbuild.lang.base.define;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.Optional;

import org.smoothbuild.fs.space.FilePath;

import com.google.common.collect.ImmutableList;

public record ModFiles(FilePath smoothFile, Optional<FilePath> nativeFile) {
  public ImmutableList<FilePath> asList() {
    if (nativeFile.isPresent()) {
      return list(smoothFile, nativeFile.get());
    } else {
      return list(smoothFile);
    }
  }
}
