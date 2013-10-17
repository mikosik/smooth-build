package org.smoothbuild.hash;

import java.nio.charset.Charset;

import org.smoothbuild.function.base.Name;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class Hash {
  public static final Charset STRING_CHARSET = Charsets.UTF_8;

  public static HashCode nativeFunction(Name name) {
    return string(name.full());
  }

  public static HashCode string(String string) {
    return hashFunction().hashString(string, STRING_CHARSET);
  }

  public static HashCode bytes(byte[] bytes) {
    return hashFunction().hashBytes(bytes);
  }

  public static HashFunction hashFunction() {
    return Hashing.sha1();
  }
}
