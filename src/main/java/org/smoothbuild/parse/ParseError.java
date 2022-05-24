package org.smoothbuild.parse;

import static org.smoothbuild.out.log.Log.error;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.parse.ast.AstNode;

public class ParseError {
  public static Log parseError(AstNode astNode, String message) {
    return parseError(astNode.loc(), message);
  }

  public static Log parseError(Loc loc, String message) {
    return error(loc.toString() + ": " + message);
  }
}
