package org.smoothbuild.bytecode.type;

import static org.smoothbuild.util.collect.Lists.allMatch;
import static org.smoothbuild.util.collect.Lists.allMatchOtherwise;

import java.util.function.Supplier;

import org.smoothbuild.bytecode.type.cnst.CallableTB;
import org.smoothbuild.bytecode.type.cnst.ComposedTB;
import org.smoothbuild.bytecode.type.cnst.NothingTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;

import com.google.common.collect.ImmutableList;

public class IsAssignable {
  public static boolean isAssignable(TypeB target, TypeB source) {
    return source instanceof NothingTB || isAssignableNonTrivial(target, source);
  }

  private static boolean isAssignableNonTrivial(TypeB target, TypeB source) {
    if (source instanceof ComposedTB sourceC) {
      if (source.getClass().equals(target.getClass())) {
        var targetC = (ComposedTB) target;
        return allMatch(targetC.covars(), sourceC.covars(), IsAssignable::isAssignable)
            && allMatch(sourceC.contravars(), targetC.contravars(), IsAssignable::isAssignable);
      } else {
        return false;
      }
    } else {
      return source.equals(target);
    }
  }

  public static void validateArgs(CallableTB callableTB,
      ImmutableList<TypeB> argTs, Supplier<RuntimeException> illegalArgsExcThrower) {
    allMatchOtherwise(
        callableTB.params(),
        argTs,
        IsAssignable::isAssignable,
        (expectedSize, actualSize) -> { throw illegalArgsExcThrower.get(); },
        i -> { throw illegalArgsExcThrower.get(); }
    );
  }
}
