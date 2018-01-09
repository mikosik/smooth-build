package org.smoothbuild.task.base;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class ComputationHashes {
  public static HashCode valueComputationHash(Value value) {
    return hash(0, value.hash());
  }

  public static HashCode arrayComputationHash() {
    return hash(1);
  }

  public static HashCode identityComputationHash() {
    return hash(2);
  }

  public static HashCode nativeCallComputationHash(NativeFunction function) {
    return hash(3, function.hash());
  }

  public static HashCode throwExceptionComputationHash() {
    return hash(4);
  }

  private static HashCode hash(int id, HashCode hash) {
    return Hash.newHasher().putInt(id).putBytes(hash.asBytes()).hash();
  }

  private static HashCode hash(int id) {
    return Hash.newHasher().putInt(id).hash();
  }
}
