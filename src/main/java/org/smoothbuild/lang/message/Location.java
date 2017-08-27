package org.smoothbuild.lang.message;

import static com.google.common.base.Preconditions.checkArgument;

public class Location {
  private final int line;

  public static Location commandLine() {
    return new Location(-1);
  }

  public static Location location(int line) {
    checkArgument(0 < line);
    return new Location(line);
  }

  private Location(int line) {
    this.line = line;
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
      return this.line == that.line;
    }
    return false;
  }

  public final int hashCode() {
    return line;
  }

  public String toString() {
    return "[ " + asString() + " ]";
  }

  private String asString() {
    if (line == -1) {
      return "cmd line";
    } else {
      return "line " + Integer.toString(line);
    }
  }
}
