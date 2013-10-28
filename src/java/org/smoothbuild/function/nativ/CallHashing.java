package org.smoothbuild.function.nativ;

import java.util.Map;

import org.smoothbuild.function.base.Param;
import org.smoothbuild.hash.Hash;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class CallHashing {
  public static HashCode hashCall(NativeFunction function, Map<String, Value> arguments) {
    Hasher hasher = Hash.function().newHasher();
    hasher.putBytes(Hash.string(function.signature().name().full()).asBytes());
    for (Param param : function.params().values()) {
      Value argument = arguments.get(param.name());
      if (argument != null) {
        hasher.putBytes(Hash.string(param.name()).asBytes());
        hasher.putBytes(argument.hash().asBytes());
      }
    }
    return hasher.hash();
  }
}
