package org.smoothbuild.common;

public class UnescapingFailedExc extends RuntimeException {
  public static UnescapingFailedExc missingEscapeCodeException(int charIndex) {
    return new UnescapingFailedExc(
        "Missing escape code after backslash \\ at char index = " + charIndex + ".");
  }

  public static UnescapingFailedExc illegalEscapeSeqException(int charIndex) {
    return new UnescapingFailedExc("Illegal escape sequence at char index = " + charIndex
        + ". Legal sequences are: \\t \\b \\n \\r \\f \\\" \\\\.");
  }

  public UnescapingFailedExc(String message) {
    super(message);
  }
}
