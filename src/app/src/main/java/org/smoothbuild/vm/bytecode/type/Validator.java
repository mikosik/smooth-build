package org.smoothbuild.vm.bytecode.type;

import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.function.Supplier;

import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;
import org.smoothbuild.vm.bytecode.type.value.TypeB;

import com.google.common.collect.ImmutableList;

public class Validator {
  public static void validateArgs(FuncTB funcTB, ImmutableList<TypeB> items,
      Supplier<RuntimeException> exceptionThrower) {
    validateTuple(funcTB.params(), items, exceptionThrower);
  }

  public static void validateTuple(TupleTB tupleTB, ImmutableList<TypeB> itemTs,
      Supplier<RuntimeException> exceptionThrower) {
    allMatchOtherwise(
        tupleTB.elements(),
        itemTs,
        CategoryB::equals,
        (expectedSize, actualSize) -> { throw exceptionThrower.get(); },
        i -> { throw exceptionThrower.get(); }
    );
  }
}
