package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Space.UNKNOWN;
import static org.smoothbuild.lang.base.Space.USER;

public record Location(ModulePath modulePath, int line) {

  public static Location commandLineLocation() {
    return new Location(ModulePath.modulePath(USER, null, null), 1);
  }

  public static Location unknownLocation() {
    return new Location(ModulePath.modulePath(UNKNOWN, null, null), -1);
  }

  public static Location location(ModulePath modulePath, int line) {
    checkArgument(0 < line);
    return new Location(modulePath, line);
  }

  public Location {
    this.modulePath = requireNonNull(modulePath);
  }

  public ModulePath module() {
    return modulePath;
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
