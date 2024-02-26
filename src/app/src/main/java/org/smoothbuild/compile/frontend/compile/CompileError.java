package org.smoothbuild.compile.frontend.compile;

import static org.smoothbuild.common.log.Log.error;

import org.smoothbuild.common.log.Log;
import org.smoothbuild.compile.frontend.lang.base.location.Located;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

public class CompileError {
  public static Log compileError(Located located, String message) {
    return compileError(located.location(), message);
  }

  public static Log compileError(Location location, String message) {
    return error(location.toString() + ": " + message);
  }
}
