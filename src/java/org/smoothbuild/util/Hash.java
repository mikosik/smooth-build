package org.smoothbuild.util;

import static com.google.common.base.Charsets.UTF_8;

import java.util.Map;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Signature;
import org.smoothbuild.function.nativ.NativeFunction;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class Hash {
  private static final int STRING_CODE = 1;
  private static final int EMPTY_SET_CODE = 2;
  private static final int FILE_SET_CODE = 3;
  private static final int STRING_SET_CODE = 4;
  private static final int CALL_CODE = 5;

  public static HashCode string(String string) {
    Hasher hasher = newHasherFor(STRING_CODE);
    hasher.putString(string, UTF_8);
    return hasher.hash();
  }

  public static HashCode nativeFunction(Signature signature) {
    String name = signature.name().full();
    return hashFunction().hashString(name, Charsets.UTF_8);
  }

  public static HashCode emptySet() {
    return hashFunction().hashInt(EMPTY_SET_CODE);
  }

  public static HashCode fileSet(Iterable<HashCode> elements) {
    return set(FILE_SET_CODE, elements);
  }

  public static HashCode stringSet(Iterable<HashCode> elements) {
    int code = STRING_SET_CODE;
    return set(code, elements);
  }

  private static HashCode set(int code, Iterable<HashCode> elements) {
    Hasher hasher = newHasherFor(code);
    for (HashCode hash : elements) {
      hasher.putBytes(hash.asBytes());
    }
    return hasher.hash();
  }

  public static HashCode call(NativeFunction function, Map<String, HashCode> arguments) {
    Hasher hasher = newHasherFor(CALL_CODE);
    hasher.putBytes(function.hash().asBytes());
    for (Param param : function.params().values()) {
      HashCode argument = arguments.get(param.name());
      if (argument != null) {
        hasher.putBytes(param.hash().asBytes());
        hasher.putBytes(argument.asBytes());
      }
    }
    return hasher.hash();
  }

  public static HashFunction hashFunction() {
    return Hashing.sha1();
  }

  private static Hasher newHasherFor(int code) {
    Hasher hasher = hashFunction().newHasher();
    hasher.putInt(code);
    return hasher;
  }
}
