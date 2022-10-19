package org.smoothbuild.vm.task;

import static java.util.Arrays.asList;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.NatFuncB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.TypeB;

public class TaskHashes {
  public static Hash taskHash(Task task) {
    return switch (task) {
      case CombineTask combineTask -> combineHash();
      case ConstTask constTask -> constHash(constTask.instB());
      case IdentityTask identityTask -> identityHash();
      case NativeCallTask nativeCallTask -> nativeCallHash(nativeCallTask.natFunc());
      case OrderTask orderTask -> orderHash(orderTask.outputT());
      case PickTask pickTask -> pickHash();
      case SelectTask selectTask -> selectHash();
    };
  }

  private static Hash pickHash() {
    return hash(0);
  }

  private static Hash combineHash() {
    return hash(1);
  }

  private static Hash nativeCallHash(NatFuncB natFunc) {
    return hash(2, natFunc.hash());
  }

  private static Hash orderHash(TypeB typeB) {
    return hash(3, typeB.hash());
  }

  private static Hash selectHash() {
    return hash(4);
  }

  private static Hash constHash(InstB instB) {
    return hash(5, instB.hash());
  }

  private static Hash identityHash() {
    return hash(6);
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
