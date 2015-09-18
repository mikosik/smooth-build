package org.smoothbuild.task.compute;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;

public class AlgorithmHashes {
  public static HashCode constantAlgorithmHash(Value value) {
    return hash(0, value.hash());
  }

  public static HashCode arrayAlgorithmHash() {
    return hash(1);
  }

  public static HashCode identityAlgorithmHash() {
    return hash(2);
  }

  public static HashCode nativeCallAlgorithmHash(NativeFunction function) {
    return hash(3, function.hash());
  }

  private static HashCode hash(int id, HashCode hash) {
    return Hash.newHasher().putInt(id).putBytes(hash.asBytes()).hash();
  }

  private static HashCode hash(int id) {
    return Hash.newHasher().putInt(id).hash();
  }
}
