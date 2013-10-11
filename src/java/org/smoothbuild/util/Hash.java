package org.smoothbuild.util;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Hash {
  public static HashFunction hashFunction() {
    return Hashing.sha1();
  }
}
