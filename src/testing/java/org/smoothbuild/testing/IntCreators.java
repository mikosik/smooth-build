package org.smoothbuild.testing;

import java.math.BigInteger;

import okio.ByteString;

public class IntCreators {
  public static ByteString intToByteString(int integer) {
    return ByteString.of(BigInteger.valueOf(integer).toByteArray());
  }
}
