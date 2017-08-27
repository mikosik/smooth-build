package org.smoothbuild.parse;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.cli.Console;
import org.smoothbuild.lang.message.Location;

public class ParseError {
  public final Location location;
  public final String message;

  public ParseError(Location location, String message) {
    this.location = requireNonNull(location);
    this.message = requireNonNull(message);
  }

  public String toString() {
    return Console.errorLine(location, message);
  }
}
