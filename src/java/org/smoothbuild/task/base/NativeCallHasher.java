package org.smoothbuild.task.base;

import java.util.Map;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.nativ.NativeFunction;
import org.smoothbuild.object.Hash;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class NativeCallHasher {
  private final NativeFunction nativeFunction;
  private final Map<String, Result> args;

  public NativeCallHasher(NativeFunction nativeFunction, Map<String, Result> args) {
    this.nativeFunction = nativeFunction;
    this.args = args;
  }

  public HashCode hash() {
    Hasher hasher = Hash.function().newHasher();
    hasher.putBytes(Hash.string(nativeFunction.signature().name().full()).asBytes());
    for (Param param : nativeFunction.params().values()) {
      Result argument = args.get(param.name());
      if (argument != null) {
        hasher.putBytes(Hash.string(param.name()).asBytes());
        hasher.putBytes(argument.result().hash().asBytes());
      }
    }
    return hasher.hash();
  }
}
