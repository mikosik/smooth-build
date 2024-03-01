package org.smoothbuild.compilerfrontend.lang.base.location;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.filesystem.space.FilePath;
import org.smoothbuild.common.filesystem.space.Space;

public record FileLocation(FilePath file, int line) implements SourceLocation {

  public FileLocation(FilePath file, int line) {
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
