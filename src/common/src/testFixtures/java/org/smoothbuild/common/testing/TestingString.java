package org.smoothbuild.common.testing;

import okio.ByteString;

public class TestingString {
  public static ByteString illegalString() {
    return ByteString.of((byte) -64);
  }
}
