package org.smoothbuild.virtualmachine.evaluate.step;

import static java.util.Arrays.asList;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class StepHashes {
  public static Hash stepHash(Step step) {
    return switch (step) {
      case CombineStep combineStep -> combineHash();
      case InvokeStep invokeStep -> invokeHash();
      case OrderStep orderStep -> orderHash(orderStep.evaluationType());
      case PickStep pickStep -> pickHash();
      case SelectStep selectStep -> selectHash();
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

  private static Hash hash(int id, Hash hash) {
    return Hash.of(asList(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(asList(Hash.of(id)));
  }
}
