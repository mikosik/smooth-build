package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(ExprS callee, ImmutableList<ExprS> args, Loc loc)
    implements OperS {
  public CallS {
    if (callee.evalT() instanceof FuncTS funcTS) {
      validateArgsSize(funcTS, args);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private static void validateArgsSize(FuncTS funcTS, ImmutableList<ExprS> args) {
    int paramsCount = funcTS.params().size();
    if (args.size() != paramsCount) {
      throw new IllegalArgumentException(
          "Call requires " + paramsCount + " but args size is " + args.size() + ".");
    }
  }

  @Override
  public TypeS evalT() {
    return ((FuncTS) callee.evalT()).res();
  }

  @Override
  public String label() {
    return "()";
  }
}
