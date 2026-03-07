package org.smoothbuild.virtualmachine.evaluate.cache;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCreateVariant;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInvoke;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;

public class OperationHashes {
  public static Hash operationHash(BOperation operation) {
    return switch (operation) {
      case BCreateTuple _ -> createTupleHash();
      case BCreateVariant _ -> createVariantHash();
      case BInvoke _ -> invokeHash();
      case BCreateArray createArray -> createArrayHash(createArray.evaluationType());
      case BArrayGet _ -> arrayGetHash();
      case BTupleGet _ -> tupleGetHash();
      default -> throw new IllegalStateException("Unexpected value: " + operation);
    };
  }

  private static Hash arrayGetHash() {
    return hash(0);
  }

  private static Hash createTupleHash() {
    return hash(1);
  }

  private static Hash invokeHash() {
    return hash(2);
  }

  private static Hash createArrayHash(BType type) {
    return hash(3, type.hash());
  }

  private static Hash tupleGetHash() {
    return hash(4);
  }

  private static Hash createVariantHash() {
    return hash(5);
  }

  private static Hash hash(int id, Hash hash) {
    return Hash.of(list(Hash.of(id), hash));
  }

  private static Hash hash(int id) {
    return Hash.of(list(Hash.of(id)));
  }
}
