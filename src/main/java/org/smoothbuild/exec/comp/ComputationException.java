package org.smoothbuild.exec.comp;

public class ComputationException extends Exception {
  public ComputationException(Throwable cause) {
    super(cause);
  }

  public ComputationException(String message, Throwable cause) {
    super(message, cause);
  }
}
