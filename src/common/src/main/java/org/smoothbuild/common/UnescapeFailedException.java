package org.smoothbuild.common;

public class UnescapeFailedException extends RuntimeException {
  public static UnescapeFailedException missingEscapeCodeException(int charIndex) {
    return new UnescapeFailedException(
        "Missing escape code after backslash \\ at char index = " + charIndex + ".");
  }

  public static UnescapeFailedException illegalEscapeSeqException(int charIndex) {
    return new UnescapeFailedException("Illegal escape sequence at char index = " + charIndex
        + ". Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
  }

  public UnescapeFailedException(String message) {
    super(message);
  }
}
