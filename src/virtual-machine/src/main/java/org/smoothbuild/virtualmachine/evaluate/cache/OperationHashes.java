package org.smoothbuild.virtualmachine.evaluate.cache;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class OperationHashes {
  public static Hash operationHash(BOperation operation) {
    return switch (operation) {
      case BCombine _ -> combineHash();
      case BChoose _ -> chooseHash();
      case BInvoke _ -> invokeHash();
      case BOrder order -> orderHash(order.evaluationType());
      case BPick _ -> pickHash();
      case BSelect _ -> selectHash();
      default -> throw new IllegalStateException("Unexpected value: " + operation);
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
