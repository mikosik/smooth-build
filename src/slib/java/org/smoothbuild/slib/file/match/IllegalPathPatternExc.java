package org.smoothbuild.slib.file.match;

public class IllegalPathPatternExc extends RuntimeException {
  public IllegalPathPatternExc(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalPathPatternExc(String message) {
    super(message);
  }
}
