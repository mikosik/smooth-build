package org.smoothbuild.vm.task;

import static java.util.Arrays.asList;

import org.smoothbuild.bytecode.expr.val.MethodB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;

public class TaskHashes {
  public static Hash combineTaskHash(TupleTB type) {
    return hash(1, type.hash());
  }

  public static Hash invokeTaskHash(MethodB method) {
    return hash(2, method.hash());
  }

  public static Hash orderTaskHash(TypeB typeB) {
    return hash(3, typeB.hash());
  }

  public static Hash selectTaskHash() {
    return hash(4);
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
