package org.smoothbuild.vm.algorithm;

import static java.util.Arrays.asList;

import org.smoothbuild.bytecode.expr.val.MethodB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;

public class AlgorithmHashes {
  public static Hash combineAlgorithmHash(TupleTB type) {
    return hash(1, type.hash());
  }

  public static Hash invokeAlgorithmHash(MethodB method) {
    return hash(2, method.hash());
  }

  public static Hash orderAlgorithmHash(TypeB typeB) {
    return hash(3, typeB.hash());
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
