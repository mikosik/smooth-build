package org.smoothbuild.virtualmachine.bytecode.kind.base;

import java.util.function.Supplier;
import org.smoothbuild.common.collect.List;

public class Validator {
  public static <T extends Throwable> void validateArgs(
      BFuncType funcType, List<BType> items, Supplier<T> exceptionSupplier) throws T {
    validateTuple(funcType.params(), items, exceptionSupplier);
  }

  public static <T extends Throwable> void validateTuple(
      BTupleType tupleType, List<BType> itemTypes, Supplier<T> exceptionSupplier) throws T {
    List<BType> expectedTypes = tupleType.elements();
    if (expectedTypes.size() != itemTypes.size()) {
      throw exceptionSupplier.get();
    }
    for (int i = 0; i < expectedTypes.size(); i++) {
      BType expectedType = expectedTypes.get(i);
      BType itemType = itemTypes.get(i);
      if (!itemType.equals(expectedType)) {
        throw exceptionSupplier.get();
      }
    }
  }
}
