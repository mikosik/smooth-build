package org.smoothbuild.lang.base.define;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.define.Space.INTERNAL;
import static org.smoothbuild.lang.base.define.Space.USER;

/**
 * This class is immutable.
 */
public record Location(ModuleLocation moduleLocation, int line) {

  public static Location commandLineLocation() {
    return new Location(ModuleLocation.moduleLocation(USER, null), 1);
  }

  public static Location internal() {
    return new Location(ModuleLocation.moduleLocation(INTERNAL, null), -1);
  }

  public static Location location(ModuleLocation moduleLocation, int line) {
    checkArgument(0 < line);
    return new Location(moduleLocation, line);
  }

  public Location(ModuleLocation moduleLocation, int line) {
    this.moduleLocation = requireNonNull(moduleLocation);
    this.line = line;
  }

  @Override
  public String toString() {
    if (moduleLocation.space() == INTERNAL) {
      return "smooth internal";
    } else if (moduleLocation.path() == null) {
      return "command line";
    } else {
      return moduleLocation.path() + ":" + line;
    }
  }
}
