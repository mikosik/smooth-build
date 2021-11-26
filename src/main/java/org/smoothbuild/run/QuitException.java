package org.smoothbuild.run;

public class QuitException extends RuntimeException {
  public QuitException(String message) {
    super(message);
  }
}
