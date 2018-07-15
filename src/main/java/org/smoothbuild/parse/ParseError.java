package org.smoothbuild.parse;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.parse.ast.Node;

public class ParseError {
  public final Location location;
  public final String message;

  public ParseError(Node node, String message) {
    this(node.location(), message);
  }

  public ParseError(Location location, String message) {
    this.location = requireNonNull(location);
    this.message = requireNonNull(message);
  }

  @Override
  public String toString() {
    return Console.errorLine(location, message);
  }
}
