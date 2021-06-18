package org.smoothbuild.lang.base.define;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.define.FileLocation.fileLocation;
import static org.smoothbuild.lang.base.define.Space.INTERNAL;
import static org.smoothbuild.lang.base.define.Space.PRJ;

/**
 * This class is immutable.
 */
public record Location(FileLocation file, int line) {

  public static Location commandLineLocation() {
    return new Location(fileLocation(PRJ, null), 1);
  }

  public static Location internal() {
    return new Location(fileLocation(INTERNAL, null), -1);
  }

  public static Location location(FileLocation fileLocation, int line) {
    checkArgument(0 < line);
    return new Location(fileLocation, line);
  }

  public Location(FileLocation file, int line) {
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
