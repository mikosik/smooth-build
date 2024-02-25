package org.smoothbuild.common;

public class DecodeHexException extends Exception {
  public static DecodeHexException expectedEvenNumberOfDigits() {
    return new DecodeHexException("Digits count is odd.");
  }

  public static DecodeHexException invalidHexDigits(String invalid) {
    return new DecodeHexException(
        "Following characters are not valid hex digits: '" + invalid + "'.");
  }

  public DecodeHexException(String message) {
    super(message);
  }
}
