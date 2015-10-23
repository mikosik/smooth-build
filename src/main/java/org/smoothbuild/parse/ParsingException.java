package org.smoothbuild.parse;

import org.smoothbuild.message.base.CodeLocation;

public class ParsingException extends RuntimeException {
  public ParsingException() {
    this(null);
  }

  public ParsingException(CodeLocation location, String message) {
    this("build.smooth:" + location.line() + ": error: " + message);
  }

  public ParsingException(String message) {
    super(message);
  }
}
