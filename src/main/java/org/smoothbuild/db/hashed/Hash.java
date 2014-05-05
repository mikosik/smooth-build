package org.smoothbuild.db.hashed;

import static org.smoothbuild.SmoothConstants.CHARSET;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class Hash {
  public static Hasher newHasher() {
    return function().newHasher();
  }

  public static HashCode string(String string) {
    return function().hashString(string, CHARSET);
  }

  public static HashCode integer(int value) {
    return function().hashInt(value);
  }

  public static HashCode bytes(byte[] bytes) {
    return function().hashBytes(bytes);
  }

  public static int size() {
    return function().bits() / 8;
  }

  private static HashFunction function() {
    return Hashing.sha1();
  }
}
