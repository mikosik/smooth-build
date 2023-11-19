package org.smoothbuild.compile.frontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.compile.frontend.lang.type.FuncTS;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

/**
 * This class is immutable.
 */
public record CallS(ExprS callee, CombineS args, Location location) implements ExprS {
  public CallS {
    if (callee.evaluationT() instanceof FuncTS funcTS) {
      validateArgsSize(funcTS, args);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private static void validateArgsSize(FuncTS funcTS, CombineS args) {
    int paramsCount = funcTS.params().size();
    int argsCount = args.elems().size();
    if (argsCount != paramsCount) {
      throw new IllegalArgumentException(
          "Call requires " + paramsCount + " but args size is " + argsCount + ".");
    }
  }

  @Override
  public TypeS evaluationT() {
    return ((FuncTS) callee.evaluationT()).result();
  }

  @Override
  public String toString() {
    var fields =
        joinToString("\n", "callee = " + callee, "args = " + args, "location = " + location);
    return "CallS(\n" + indent(fields) + "\n)";
  }
}
