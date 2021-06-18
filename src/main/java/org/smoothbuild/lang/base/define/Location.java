package org.smoothbuild.lang.base.define;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.define.FilePath.filePath;
import static org.smoothbuild.lang.base.define.Space.INTERNAL;
import static org.smoothbuild.lang.base.define.Space.PRJ;

/**
 * This class is immutable.
 */
public record Location(FilePath file, int line) {

  public static Location commandLineLocation() {
    return new Location(filePath(PRJ, null), 1);
  }

  public static Location internal() {
    return new Location(filePath(INTERNAL, null), -1);
  }

  public static Location location(FilePath filePath, int line) {
    checkArgument(0 < line);
    return new Location(filePath, line);
  }

  public Location(FilePath file, int line) {
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
