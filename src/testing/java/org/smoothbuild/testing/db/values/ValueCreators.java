package org.smoothbuild.testing.db.values;

import okio.ByteString;

public class ValueCreators {
  public static ByteString trueByteString() {
    return ByteString.of((byte) 1);
  }

  public static ByteString falseByteString() {
    return ByteString.of((byte) 0);
  }
}
