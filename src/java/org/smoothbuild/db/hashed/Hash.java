package org.smoothbuild.db.hashed;

import static org.smoothbuild.SmoothContants.CHARSET;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Hash {
  public static HashCode string(String string) {
    return Hash.function().hashString(string, CHARSET);
  }

  public static HashCode integer(int value) {
    return Hash.function().hashInt(value);
  }

  public static HashCode bytes(byte[] bytes) {
    return Hash.function().hashBytes(bytes);
  }

  public static int size() {
    return Hash.function().bits() / 8;
  }

  public static HashFunction function() {
    return Hashing.sha1();
  }

}
