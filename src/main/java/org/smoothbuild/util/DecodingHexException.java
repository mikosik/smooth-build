package org.smoothbuild.util;

public class DecodingHexException extends Exception {
  public static DecodingHexException expectedEvenNumberOfDigits() {
    return new DecodingHexException("Expected even number of digits.");
  }

  public static DecodingHexException invalidHexDigits(String invalid) {
    return new DecodingHexException(
        "Following characters are not valid hex digits: '" + invalid + "'.");
  }

  public DecodingHexException(String message) {
    super(message);
  }
}
