package org.smoothbuild.lang.define;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.Objects;
import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(TypeS type, ExprS callee, ImmutableList<ExprS> args, Loc loc)
    implements OperatorS {
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
  public String name() {
    return "()";
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new CallS(type().mapVars(mapper), callee.mapVars(mapper),
        map(args, a -> a.mapVars(mapper)), loc);
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
