package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Space.UNKNOWN;
import static org.smoothbuild.lang.base.Space.USER;

import java.util.Objects;

/**
 * This class is immutable.
 */
public class Location {
  private final ModulePath modulePath;
  private final int line;

  public static Location commandLineLocation() {
    return new Location(new ModulePath(USER, null, null), 1);
  }

  public static Location unknownLocation() {
    return new Location(new ModulePath(UNKNOWN, null, null), -1);
  }

  public static Location location(ModulePath modulePath, int line) {
    checkArgument(0 < line);
    return new Location(modulePath, line);
  }

  private Location(ModulePath modulePath, int line) {
    this.modulePath = requireNonNull(modulePath);
    this.line = line;
  }

  public ModulePath module() {
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
    if (modulePath.space() == UNKNOWN) {
      return "unknown location";
    } else if (modulePath.smooth().path() == null) {
      return "command line";
    } else {
      return modulePath.smooth().shorted() + ":" + line;
    }
  }
}
