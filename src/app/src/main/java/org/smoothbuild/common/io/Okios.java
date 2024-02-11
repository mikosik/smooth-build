package org.smoothbuild.common.io;

import java.math.BigInteger;
import okio.ByteString;

public class Okios {
  public static ByteString intToByteString(int data) {
    return ByteString.of(BigInteger.valueOf(data).toByteArray());
  }
}
