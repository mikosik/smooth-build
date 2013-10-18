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
    return Hash.function().hashString(string, STRING_CHARSET);
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
