package org.smoothbuild.builtin.file.match;

public class IllegalPathPatternException extends RuntimeException {
  public IllegalPathPatternException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalPathPatternException(String message) {
    super(message);
  }
}
