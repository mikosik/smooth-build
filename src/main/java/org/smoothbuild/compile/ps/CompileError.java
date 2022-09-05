package org.smoothbuild.compile.ps;

import static org.smoothbuild.out.log.Log.error;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.out.log.Log;

public class CompileError {
  public static Log compileError(WithLoc withLoc, String message) {
    return compileError(withLoc.loc(), message);
  }

  public static Log compileError(Loc loc, String message) {
    return error(loc.toString() + ": " + message);
  }
}
