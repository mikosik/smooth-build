package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.lang.base.Space.INTERNAL;
import static org.smoothbuild.lang.base.Space.USER;

public record Location(ModuleInfo moduleInfo, int line) {

  public static Location commandLineLocation() {
    return new Location(ModuleInfo.moduleInfo(USER, null, null), 1);
  }

  public static Location internal() {
    return new Location(ModuleInfo.moduleInfo(INTERNAL, null, null), -1);
  }

  public static Location location(ModuleInfo moduleInfo, int line) {
    checkArgument(0 < line);
    return new Location(moduleInfo, line);
  }

  public Location {
    this.moduleInfo = requireNonNull(moduleInfo);
  }

  public ModuleInfo module() {
    return moduleInfo;
  }

  @Override
  public String toString() {
    if (moduleInfo.space() == INTERNAL) {
      return "smooth internal";
    } else if (moduleInfo.smooth().path() == null) {
      return "command line";
    } else {
      return moduleInfo.smooth().shorted() + ":" + line;
    }
  }
}
