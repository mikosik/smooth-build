package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import org.smoothbuild.ModulePath;

public class Location {
  private final ModulePath path;
  private final int line;

  public static Location unknownLocation() {
    return new Location(null, 1);
  }

  public static Location location(ModulePath file, int line) {
    return new Location(file, line);
  }

  private Location(ModulePath path, int line) {
    checkArgument(0 < line);
    this.path = path;
    this.line = line;
  }

  public ModulePath path() {
    return path;
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
      return Objects.equals(this.path, that.path) && this.line == that.line;
    }
    return false;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(path, line);
  }

  @Override
  public String toString() {
    if (path == null) {
      return "unknown location";
    } else {
      return path.shortPath() + ":" + line;
    }
  }
}
