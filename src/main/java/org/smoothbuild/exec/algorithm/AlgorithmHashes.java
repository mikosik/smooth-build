package org.smoothbuild.exec.algorithm;

import static java.util.Arrays.asList;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.IntH;
import org.smoothbuild.db.object.obj.val.NatFuncH;
import org.smoothbuild.db.object.type.base.SpecH;
import org.smoothbuild.db.object.type.val.TupleTypeH;

public class AlgorithmHashes {
  public static Hash orderAlgorithmHash() {
    return hash(0);
  }

  public static Hash invokeAlgorithmHash(NatFuncH natFuncH) {
    return hash(1, natFuncH.hash());
  }

  public static Hash convertAlgorithmHash(SpecH destinationType) {
    return hash(2, destinationType.hash());
  }

  public static Hash constructAlgorithmHash(TupleTypeH type) {
    return hash(3, type.hash());
  }

  public static Hash selectAlgorithmHash(IntH itemIndex) {
    return hash(4, itemIndex.hash());
  }

  // TODO unused 5

  public static Hash constAlgorithmHash(ValueH valueH) {
    return hash(6, valueH.hash());
  }

  // TODO unused 7

  // TODO unused 8

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
