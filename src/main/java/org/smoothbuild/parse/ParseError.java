package org.smoothbuild.parse;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.parse.ast.Node;

public class ParseError {
  public static String parseError(Node node, String message) {
    return parseError(node.location(), message);
  }

  public static String parseError(Location location, String message) {
    return location.toString() + ": error: " + message;
  }
}
