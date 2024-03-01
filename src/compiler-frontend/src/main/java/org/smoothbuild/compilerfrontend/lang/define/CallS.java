package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.FuncTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

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
        list("callee = " + callee, "args = " + args, "location = " + location).toString("\n");
    return "CallS(\n" + indent(fields) + "\n)";
  }
}
