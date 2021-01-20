package org.smoothbuild.lang.parse;

import static org.smoothbuild.cli.console.Log.error;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.parse.ast.Node;

public class ParseError {
  public static Log parseError(Node node, String message) {
    return parseError(node.location(), message);
  }

  public static Log parseError(Location location, String message) {
    return error(location.toString() + ": " + message);
  }
}
