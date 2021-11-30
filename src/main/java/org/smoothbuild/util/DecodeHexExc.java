package org.smoothbuild.util;

public class DecodeHexExc extends Exception {
  public static DecodeHexExc expectedEvenNumberOfDigits() {
    return new DecodeHexExc("Expected even number of digits.");
  }

  public static DecodeHexExc invalidHexDigits(String invalid) {
    return new DecodeHexExc(
        "Following characters are not valid hex digits: '" + invalid + "'.");
  }

  public DecodeHexExc(String message) {
    super(message);
  }
}
