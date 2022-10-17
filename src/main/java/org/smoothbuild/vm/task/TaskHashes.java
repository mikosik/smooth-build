package org.smoothbuild.vm.task;

import static java.util.Arrays.asList;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class TaskHashes {
  public static Hash pickTaskHash() {
    return hash(0);
  }

  public static Hash combineTaskHash(TypeB tupleT) {
    return hash(1, tupleT.hash());
  }

  public static Hash nativeCallTaskHash(NatFuncB natFunc) {
    return hash(2, natFunc.hash());
  }

  public static Hash orderTaskHash(TypeB typeB) {
    return hash(3, typeB.hash());
  }

  public static Hash selectTaskHash() {
    return hash(4);
  }

  public static Hash constTaskHash(InstB instB) {
    return hash(5, instB.hash());
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
