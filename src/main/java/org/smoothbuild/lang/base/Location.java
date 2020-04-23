package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

public class Location {
  private final ModulePath modulePath;
  private final int line;

  public static Location unknownLocation() {
    return new Location(null, 1);
  }

  public static Location location(ModulePath modulePath, int line) {
    return new Location(modulePath, line);
  }

  private Location(ModulePath modulePath, int line) {
    checkArgument(0 < line);
    this.modulePath = modulePath;
    this.line = line;
  }

  public ModulePath path() {
    return modulePath;
  }

  /**
   * Line in build script. (first line = 1)
   */
  public int line() {
    return line;
  }

  @Override
  public final boolean equals(Object object) {
    if (object instanceof Location) {
      Location that = (Location) object;
      return Objects.equals(this.modulePath, that.modulePath) && this.line == that.line;
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(modulePath, line);
  }

  @Override
  public String toString() {
    if (modulePath == null) {
      return "unknown location";
    } else {
      return modulePath.shortPath() + ":" + line;
    }
  }
}
