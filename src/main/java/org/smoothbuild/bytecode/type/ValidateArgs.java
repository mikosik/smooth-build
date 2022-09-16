package org.smoothbuild.bytecode.type;

import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.function.Supplier;

import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TypeB;

import com.google.common.collect.ImmutableList;

public class ValidateArgs {
  public static void validateArgs(FuncTB funcTB, ImmutableList<TypeB> items,
      Supplier<RuntimeException> illegalArgsExcThrower) {
    allMatchOtherwise(
        funcTB.params().items(),
        items,
        CatB::equals,
        (expectedSize, actualSize) -> { throw illegalArgsExcThrower.get(); },
        i -> { throw illegalArgsExcThrower.get(); }
    );
  }
}
