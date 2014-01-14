package org.smoothbuild.lang.function.base;

import java.util.Map;

import org.smoothbuild.io.cache.hash.Hash;
import org.smoothbuild.task.base.Result;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class CallHasher {
  private final Function function;
  private final Map<String, Result> args;

  public CallHasher(Function function, Map<String, Result> args) {
    this.function = function;
    this.args = args;
  }

  public HashCode hash() {
    Hasher hasher = Hash.function().newHasher();
    hasher.putBytes(Hash.string(function.signature().name().value()).asBytes());
    for (Param param : function.params().values()) {
      Result argument = args.get(param.name());
      if (argument != null) {
        HashCode paramNameHash = param.nameHash();
        HashCode valueHash = argument.value().hash();

        hasher.putBytes(paramNameHash.asBytes());
        hasher.putBytes(valueHash.asBytes());
      }
    }
    return hasher.hash();
  }
}
