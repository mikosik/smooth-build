package org.smoothbuild.common.testing;

import okio.ByteString;

public class TestingByteString {
  public static ByteString byteStringWithSingleByteEqualZero() {
    return ByteString.of((byte) 0);
  }

  public static ByteString byteStringWithSingleByteEqualOne() {
    return ByteString.of((byte) 1);
  }
}
