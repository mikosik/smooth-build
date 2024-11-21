package org.smoothbuild.common.log.location;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.filesystem.base.FullPath;

public record FileLocation(FullPath path, int line) implements SourceLocation {

  public FileLocation(FullPath path, int line) {
    this.path = requireNonNull(path);
    this.line = line;
  }

  @Override
  public String toString() {
    return path + ":" + line;
  }
}
