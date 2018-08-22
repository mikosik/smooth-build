package org.smoothbuild.db.hashed;

public class NotEnoughBytesException extends Exception {
  public NotEnoughBytesException(int expected, int available) {
    super("Expected " + expected + " bytes but only " + available + " bytes is available.");
  }
}
