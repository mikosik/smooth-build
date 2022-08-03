package org.smoothbuild.parse;

import static org.smoothbuild.out.log.Log.error;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.WithLoc;
import org.smoothbuild.out.log.Log;

public class ParseError {
  public static Log parseError(WithLoc withLoc, String message) {
    return parseError(withLoc.loc(), message);
  }

  public static Log parseError(Loc loc, String message) {
    return error(loc.toString() + ": " + message);
  }
}
