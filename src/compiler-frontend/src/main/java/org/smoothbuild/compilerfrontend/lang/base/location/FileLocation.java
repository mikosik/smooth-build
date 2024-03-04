package org.smoothbuild.compilerfrontend.lang.base.location;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.filesystem.space.FullPath;
import org.smoothbuild.common.filesystem.space.Space;

public record FileLocation(FullPath file, int line) implements SourceLocation {

  public FileLocation(FullPath file, int line) {
    this.file = requireNonNull(file);
    this.line = line;
  }

  @Override
  public Space space() {
    return file().space();
  }

  @Override
  public String toString() {
    return file + ":" + line;
  }
}
