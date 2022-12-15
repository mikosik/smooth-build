package org.smoothbuild.vm.evaluate.task;

import static java.util.Arrays.asList;

import org.smoothbuild.vm.bytecode.expr.value.NativeFuncB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class TaskHashes {
  public static Hash taskHash(Task task) {
    return switch (task) {
      case CombineTask combineTask -> combineHash();
      case ConstTask constTask -> constHash(constTask.valueB());
      case InvokeTask invokeTask -> invokeHash(invokeTask.nativeFunc());
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

  private static Hash invokeHash(NativeFuncB nativeFuncB) {
    return hash(2, nativeFuncB.hash());
  }

  private static Hash orderHash(TypeB typeB) {
    return hash(3, typeB.hash());
  }

  private static Hash selectHash() {
    return hash(4);
  }

  private static Hash constHash(ValueB valueB) {
    return hash(5, valueB.hash());
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
