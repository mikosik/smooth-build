package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Space.INTERNAL;
import static org.smoothbuild.lang.base.Space.USER;

public record Location(ModulePath modulePath, int line) {

  public static Location commandLineLocation() {
    return new Location(ModulePath.modulePath(USER, null, null), 1);
  }

  public static Location internal() {
    return new Location(ModulePath.modulePath(INTERNAL, null, null), -1);
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
    if (modulePath.space() == INTERNAL) {
      return "smooth internal";
    } else if (modulePath.smooth().path() == null) {
      return "command line";
    } else {
      return modulePath.smooth().shorted() + ":" + line;
    }
  }
}
