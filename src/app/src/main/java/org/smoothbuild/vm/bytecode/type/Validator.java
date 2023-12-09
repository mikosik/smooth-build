package org.smoothbuild.vm.bytecode.type;

import java.util.function.Supplier;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

public class Validator {
  public static void validateArgs(
      FuncTB funcTB, List<TypeB> items, Supplier<RuntimeException> exceptionThrower) {
    validateTuple(funcTB.params(), items, exceptionThrower);
  }

  public static void validateTuple(
      TupleTB tupleTB, List<TypeB> itemTypes, Supplier<RuntimeException> exceptionSupplier) {
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
