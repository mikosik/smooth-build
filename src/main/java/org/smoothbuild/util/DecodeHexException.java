package org.smoothbuild.util;

public class DecodeHexException extends Exception {
  public static DecodeHexException expectedEvenNumberOfDigits() {
    return new DecodeHexException("Expected even number of digits.");
  }

  public static DecodeHexException invalidHexDigits(String invalid) {
    return new DecodeHexException(
        "Following characters are not valid hex digits: '" + invalid + "'.");
  }

  public DecodeHexException(String message) {
    super(message);
  }
}
