package org.smoothbuild.lang.message;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

public class Location {
  private final String file;
  private final int line;

  public static Location commandLine() {
    return new Location(null, -1);
  }

  public static Location location(String file, int line) {
    checkArgument(0 < line);
    return new Location(file, line);
  }

  private Location(String file, int line) {
    this.file = file;
    this.line = line;
  }

  public String file() {
    return file;
  }

  /**
   * Line in build script. (first line = 1)
   */
  public int line() {
    return line;
  }

  public final boolean equals(Object object) {
    if (object instanceof Location) {
      Location that = (Location) object;
      return Objects.equals(this.file, that.file) && this.line == that.line;
    }
    return false;
  }

  public final int hashCode() {
    return Objects.hash(file, line);
  }

  public String toString() {
    if (line == -1) {
      return "cmd line";
    } else {
      return file + ":" + line;
    }
  }
}
