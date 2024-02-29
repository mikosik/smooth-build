package org.smoothbuild.common.testing;

import okio.ByteString;

public class BooleanCreators {
  public static ByteString trueByteString() {
    return ByteString.of((byte) 1);
  }

  public static ByteString falseByteString() {
    return ByteString.of((byte) 0);
  }
}
