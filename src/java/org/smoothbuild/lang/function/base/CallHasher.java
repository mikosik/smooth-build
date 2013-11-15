package org.smoothbuild.lang.function.base;

import java.util.Map;

import org.smoothbuild.io.db.hash.Hash;
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
        hasher.putBytes(Hash.string(param.name()).asBytes());
        hasher.putBytes(argument.result().hash().asBytes());
      }
    }
    return hasher.hash();
  }
}
