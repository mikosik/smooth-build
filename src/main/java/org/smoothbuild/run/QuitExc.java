package org.smoothbuild.run;

public class QuitExc extends RuntimeException {
  public QuitExc(String message) {
    super(message);
  }
}
