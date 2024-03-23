package org.smoothbuild.virtualmachine.bytecode.type;

import java.util.function.Supplier;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.type.value.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BTupleType;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;

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
