package org.smoothbuild.util;

public class UnescapingFailedException extends RuntimeException {
  private final int charIndex;

  public static UnescapingFailedException missingEscapeCodeException(int charIndex) {
    return new UnescapingFailedException(charIndex, "Missing escape code after backslash \\");
  }

  public static UnescapingFailedException illegalEscapeSequenceException(int charIndex) {
    return new UnescapingFailedException(charIndex,
        "Illegal escape sequence. Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
  }

  public UnescapingFailedException(int charIndex, String message) {
    super(message);
    this.charIndex = charIndex;
  }

  public int charIndex() {
    return charIndex;
  }
}
