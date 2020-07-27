package org.smoothbuild.util;

import static com.google.common.base.CharMatcher.noneOf;
import static okio.ByteString.decodeHex;

import com.google.common.base.CharMatcher;

import okio.ByteString;

public class Hex {
  private static final CharMatcher INVALID_CHAR_MATCHER = noneOf("0123456789ABCDEFabcdef");

  public static ByteString decode(String string) throws DecodingHexException {
    String retained = INVALID_CHAR_MATCHER.retainFrom(string);
    if (!retained.isEmpty()) {
      throw DecodingHexException.invalidHexDigits(retained);
    }
    if (string.length() % 2 != 0) {
      throw DecodingHexException.expectedEvenNumberOfDigits();
    }
    return decodeHex(string);
  }
}
