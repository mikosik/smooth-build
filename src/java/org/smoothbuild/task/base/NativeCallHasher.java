package org.smoothbuild.task.base;

import java.util.Map;

import org.smoothbuild.db.hash.Hash;
import org.smoothbuild.function.base.Function;
import org.smoothbuild.function.base.Param;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class NativeCallHasher {
  private final Function function;
  private final Map<String, Result> args;

  public NativeCallHasher(Function function, Map<String, Result> args) {
    this.function = function;
    this.args = args;
  }

  public HashCode hash() {
    Hasher hasher = Hash.function().newHasher();
    hasher.putBytes(Hash.string(function.signature().name().value()).asBytes());
    for (Param param : function.params().values()) {
      Result argument = args.get(param.name());
      if (argument != null) {
        hasher.putBytes(Hash.string(param.name()).asBytes());
        hasher.putBytes(argument.result().hash().asBytes());
      }
    }
    return hasher.hash();
  }
}
