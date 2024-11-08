package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.log.base.Log.error;

import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.location.Located;
import org.smoothbuild.common.log.location.Location;

public class CompileError {
  public static Log compileError(Located located, String message) {
    return compileError(located.location(), message);
  }

  public static Log compileError(Location location, String message) {
    return error(location.toString() + ": " + message);
  }
}
