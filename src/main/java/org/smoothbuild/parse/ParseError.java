package org.smoothbuild.parse;

import static org.smoothbuild.out.log.Log.error;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.parse.ast.Node;

public class ParseError {
  public static Log parseError(Node node, String message) {
    return parseError(node.loc(), message);
  }

  public static Log parseError(Loc loc, String message) {
    return error(loc.toString() + ": " + message);
  }
}
