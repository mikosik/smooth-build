package org.smoothbuild.exec.algorithm;

import static java.util.Arrays.asList;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.MethodH;
import org.smoothbuild.db.object.type.val.TupleTH;

public class AlgorithmHashes {
  public static Hash combineAlgorithmHash(TupleTH type) {
    return hash(0, type.hash());
  }

  // TODO 1 is unused

  public static Hash invokeAlgorithmHash(MethodH method) {
    return hash(2, method.hash());
  }

  public static Hash orderAlgorithmHash() {
    return hash(3);
  }

  public static Hash selectAlgorithmHash() {
    return hash(4);
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
