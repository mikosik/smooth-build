package org.smoothbuild.parse;

public class ParsingException extends RuntimeException {
  public ParsingException() {
    this(null);
  }

  public ParsingException(String message) {
    super(message);
  }
}
