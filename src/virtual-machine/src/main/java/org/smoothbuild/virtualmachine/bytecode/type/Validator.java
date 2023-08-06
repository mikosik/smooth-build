package org.smoothbuild.virtualmachine.bytecode.type;

import java.util.function.Supplier;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;

public class Validator {
  public static <T extends Throwable> void validateArgs(
      FuncTB funcTB, List<TypeB> items, Supplier<T> exceptionSupplier) throws T {
    validateTuple(funcTB.params(), items, exceptionSupplier);
  }

  public static <T extends Throwable> void validateTuple(
      TupleTB tupleTB, List<TypeB> itemTypes, Supplier<T> exceptionSupplier) throws T {
    List<TypeB> expectedTypes = tupleTB.elements();
    if (expectedTypes.size() != itemTypes.size()) {
      throw exceptionSupplier.get();
    }
    for (int i = 0; i < expectedTypes.size(); i++) {
      TypeB expectedType = expectedTypes.get(i);
      TypeB itemType = itemTypes.get(i);
      if (!itemType.equals(expectedType)) {
        throw exceptionSupplier.get();
      }
    }
  }
}
