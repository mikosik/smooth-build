package org.smoothbuild.virtualmachine.evaluate.task;

import static java.util.Arrays.asList;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BNativeFunc;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.base.BType;

public class TaskHashes {
  public static Hash taskHash(Task task) {
    return switch (task) {
      case CombineTask combineTask -> combineHash();
      case ConstTask constTask -> constHash(constTask.valueB());
      case InvokeTask invokeTask -> invokeHash(invokeTask.nativeFunc());
      case OrderTask orderTask -> orderHash(orderTask.outputType());
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

  private static Hash invokeHash(BNativeFunc nativeFunc) {
    return hash(2, nativeFunc.hash());
  }

  private static Hash orderHash(BType type) {
    return hash(3, type.hash());
  }

  private static Hash selectHash() {
    return hash(4);
  }

  private static Hash constHash(BValue value) {
    return hash(5, value.hash());
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
