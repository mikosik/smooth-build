package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SFuncType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * This class is immutable.
 */
public record SCall(SExpr callee, SCombine args, Location location) implements SExpr {
  public SCall {
    if (callee.evaluationType() instanceof SFuncType sFuncType) {
      validateArgsSize(sFuncType, args);
    } else {
      throw new IllegalArgumentException();
    }
  }

  private static void validateArgsSize(SFuncType sFuncType, SCombine args) {
    int paramsCount = sFuncType.params().size();
    int argsCount = args.elements().size();
    if (argsCount != paramsCount) {
      throw new IllegalArgumentException(
          "Call requires " + paramsCount + " but args size is " + argsCount + ".");
    }
  }

  @Override
  public SType evaluationType() {
    return ((SFuncType) callee.evaluationType()).result();
  }

  @Override
  public String toString() {
    var fields =
        list("callee = " + callee, "args = " + args, "location = " + location).toString("\n");
    return "SCall(\n" + indent(fields) + "\n)";
  }
}
