package org.smoothbuild.exec.algorithm;

import static java.util.Arrays.asList;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

public class AlgorithmHashes {
  public static Hash combineAlgorithmHash(TupleTypeH type) {
    return hash(0, type.hash());
  }

  public static Hash constAlgorithmHash(ValH val) {
    return hash(1, val.hash());
  }

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
