package org.smoothbuild.util;

@SuppressWarnings("serial")
public class UnescapingFailedException extends RuntimeException {
  private final int charIndex;

  public UnescapingFailedException(int charIndex, String message) {
    super(message);
    this.charIndex = charIndex;
  }

  public int charIndex() {
    return charIndex;
  }
}
