package org.smoothbuild.lang.base.define;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.io.fs.space.FilePath.filePath;
import static org.smoothbuild.io.fs.space.Space.INTERNAL;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import org.smoothbuild.io.fs.space.FilePath;

/**
 * Location.
 * This class is immutable.
 */
public record Loc(FilePath file, int line) {

  public static Loc commandLineLoc() {
    return new Loc(filePath(PRJ, null), 1);
  }

  public static Loc internal() {
    return new Loc(filePath(INTERNAL, null), -1);
  }

  public static Loc loc(FilePath filePath, int line) {
    checkArgument(0 < line);
    return new Loc(filePath, line);
  }

  public Loc(FilePath file, int line) {
    this.file = requireNonNull(file);
    this.line = line;
  }

  @Override
  public String toString() {
    if (file.space() == INTERNAL) {
      return "smooth internal";
    } else if (file.path() == null) {
      return "command line";
    } else {
      return file.path() + ":" + line;
    }
  }
}
