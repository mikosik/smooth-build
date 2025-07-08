package org.smoothbuild.virtualmachine.evaluate.step;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class StepHashes {
  public static Hash stepHash(Step step) {
    return switch (step) {
      case CombineStep _ -> combineHash();
      case ChooseStep _ -> chooseHash();
      case InvokeStep _ -> invokeHash();
      case OrderStep orderStep -> orderHash(orderStep.evaluationType());
      case PickStep _ -> pickHash();
      case SelectStep _ -> selectHash();
    };
  }

  private static Hash pickHash() {
    return hash(0);
  }

  private static Hash combineHash() {
    return hash(1);
  }

  private static Hash invokeHash() {
    return hash(2);
  }

  private static Hash orderHash(BType type) {
    return hash(3, type.hash());
  }

  private static Hash selectHash() {
    return hash(4);
  }

  private static Hash chooseHash() {
    return hash(5);
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(list(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(list(Hash.of(id)));
  }
}
