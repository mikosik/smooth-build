package org.smoothbuild.util;

public class UnescapingFailedException extends RuntimeException {
  public static UnescapingFailedException missingEscapeCodeException(int charIndex) {
    return new UnescapingFailedException(
        "Missing escape code after backslash \\ at char index = " + charIndex + ".");
  }

  public static UnescapingFailedException illegalEscapeSequenceException(int charIndex) {
    return new UnescapingFailedException("Illegal escape sequence at char index = " + charIndex
        + ". Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
  }

  public UnescapingFailedException(String message) {
    super(message);
  }
}
