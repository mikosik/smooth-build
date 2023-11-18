package org.smoothbuild.compile.frontend.lang.base.location;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.Space;

public record FileLocation(FilePath file, int line)
    implements SourceLocation {

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
    return file.path() + ":" + line;
  }
}
