package org.smoothbuild.parse;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.message.CodeLocation;

public class ParseError {
  public final CodeLocation codeLocation;
  public final String message;

  public ParseError(CodeLocation codeLocation, String message) {
    this.codeLocation = requireNonNull(codeLocation);
    this.message = requireNonNull(message);
  }
}
