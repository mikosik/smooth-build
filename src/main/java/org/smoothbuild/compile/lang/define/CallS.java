package org.smoothbuild.compile.lang.define;

import java.util.Objects;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(TypeS type, ExprS callee, ImmutableList<ExprS> args, Loc loc)
    implements OperS {
  public CallS {
    validateArgsSize(callee, args);
  }

  private static void validateArgsSize(ExprS callee, ImmutableList<ExprS> args) {
    int paramsCount = ((FuncTS) callee.type()).params().size();
    if (args.size() != paramsCount) {
      throw new IllegalArgumentException(
          "Call requires " + paramsCount + " but args size is " + args.size() + ".");
    }
  }

  @Override
  public String label() {
    return "()";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CallS callS)) {
      return false;
    }
    return type.equals(callS.type)
        && callee.equals(callS.callee)
        && args.equals(callS.args)
        && loc.equals(callS.loc);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, callee, args, loc);
  }
}
