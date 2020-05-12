package org.smoothbuild.slib.file.match;

public class IllegalPathPatternException extends RuntimeException {
  public IllegalPathPatternException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalPathPatternException(String message) {
    super(message);
  }
}
