package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.log.base.Log.error;

import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;

public class CompileError {
  public static Log compileError(HasLocation hasLocation, String message) {
    return compileError(hasLocation.location(), message);
  }

  public static Log compileError(Location location, String message) {
    return error(location.toString() + ": " + message);
  }
}
