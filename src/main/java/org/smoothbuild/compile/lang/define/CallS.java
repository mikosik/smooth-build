package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallS(ExprS callee, ImmutableList<ExprS> args, Loc loc)
    implements ExprS {
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
  public String toString() {
    var fields = joinToString("\n",
        "callee = " + callee,
        "args = [\n" + indent(joinToString(args, "\n")) + "\n]",
        "loc = " + loc
    );
    return "CallS(\n" + indent(fields) + "\n)";
  }
}
