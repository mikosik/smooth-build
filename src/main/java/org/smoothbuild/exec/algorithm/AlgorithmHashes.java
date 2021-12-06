package org.smoothbuild.exec.algorithm;

import static java.util.Arrays.asList;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.type.val.TupleTH;

public class AlgorithmHashes {
  public static Hash combineAlgorithmHash(TupleTH type) {
    return hash(0, type.hash());
  }

  // TODO 1 is unused

  public static Hash invokeAlgorithmHash(NatFuncH natFuncH) {
    return hash(2, natFuncH.hash());
  }

  public static Hash orderAlgorithmHash() {
    return hash(3);
  }

  public static Hash selectAlgorithmHash(IntH itemIndex) {
    return hash(4, itemIndex.hash());
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
