package org.smoothbuild.compile.lang.base;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.fs.space.FilePath.filePath;
import static org.smoothbuild.fs.space.Space.INTERNAL;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.fs.space.Space.UNKNOWN;

import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.Space;

/**
 * Location.
 * This class is immutable.
 */
public record Loc(FilePath file, int line) {
  private static final Loc INTERNAL_LOC = new Loc(filePath(INTERNAL, null), -1);
  private static final Loc UNKNOWN_LOC = new Loc(filePath(UNKNOWN, null), -1);

  public static Loc commandLineLoc() {
    return new Loc(filePath(PRJ, null), 1);
  }

  public static Loc internal() {
    return INTERNAL_LOC;
  }

  public static Loc unknown() {
    return UNKNOWN_LOC;
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
    Space space = file.space();
    if (space == INTERNAL) {
      return "internal";
    } else if (space == UNKNOWN) {
      return "unknown";
    } else if (file.path() == null) {
      return "command line";
    } else {
      return file.path() + ":" + line;
    }
  }
}
