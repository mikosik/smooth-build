package org.smoothbuild.compile.fs.ps;

import static org.smoothbuild.out.log.Log.error;

import org.smoothbuild.compile.fs.lang.base.location.Located;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.out.log.Log;

public class CompileError {
  public static Log compileError(Located located, String message) {
    return compileError(located.location(), message);
  }

  public static Log compileError(Location location, String message) {
    return error(location.toString() + ": " + message);
  }
}
