package org.smoothbuild.compile.ps;

import static org.smoothbuild.out.log.Log.error;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Located;
import org.smoothbuild.out.log.Log;

public class CompileError {
  public static Log compileError(Located located, String message) {
    return compileError(located.loc(), message);
  }

  public static Log compileError(Loc loc, String message) {
    return error(loc.toString() + ": " + message);
  }
}
