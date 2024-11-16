package org.smoothbuild.common.log.location;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FullPath;

public record FileLocation(FullPath file, int line) implements SourceLocation {

  public FileLocation(FullPath file, int line) {
    this.file = requireNonNull(file);
    this.line = line;
  }

  public Alias alias() {
    return file().alias();
  }

  @Override
  public String toString() {
    return file + ":" + line;
  }
}
