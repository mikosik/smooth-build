package org.smoothbuild.lang.parse;

import static org.smoothbuild.out.log.Log.error;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.parse.ast.Node;
import org.smoothbuild.out.log.Log;

public class ParseError {
  public static Log parseError(Node node, String message) {
    return parseError(node.loc(), message);
  }

  public static Log parseError(Loc loc, String message) {
    return error(loc.toString() + ": " + message);
  }
}
