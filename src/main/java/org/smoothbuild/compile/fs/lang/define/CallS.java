package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.FuncTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * This class is immutable.
 */
public record CallS(ExprS callee, CombineS args, Location location)
    implements ExprS {
  public CallS {
    if (callee.evalT() instanceof FuncTS funcTS) {
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
  public TypeS evalT() {
    return ((FuncTS) callee.evalT()).result();
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "callee = " + callee,
        "args = " + args,
        "location = " + location
    );
    return "CallS(\n" + indent(fields) + "\n)";
  }
}
