package org.smoothbuild.parse;

import static org.smoothbuild.out.log.Log.error;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.parse.ast.Parsed;

public class ParseError {
  public static Log parseError(Parsed parsed, String message) {
    return parseError(parsed.loc(), message);
  }

  public static Log parseError(Loc loc, String message) {
    return error(loc.toString() + ": " + message);
  }
}
