package org.smoothbuild.lang.parse;

import static org.smoothbuild.cli.console.Log.error;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.parse.ast.Node;

public class ParseError {
  public static Log parseError(Node node, String message) {
    return parseError(node.loc(), message);
  }

  public static Log parseError(Loc loc, String message) {
    return error(loc.toString() + ": " + message);
  }
}
