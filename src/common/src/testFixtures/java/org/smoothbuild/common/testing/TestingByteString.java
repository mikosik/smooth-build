package org.smoothbuild.common.testing;

import okio.ByteString;

public class TestingByteString {
  public static ByteString byteStringWithSingleByteEqualZero() {
    return ByteString.of((byte) 0);
  }

  public static ByteString byteStringWithSingleByteEqualOne() {
    return ByteString.of((byte) 1);
  }

  public static ByteString byteString() {
    return byteString("abc");
  }

  public static ByteString byteString(String string) {
    return ByteString.encodeUtf8(string);
  }
}
