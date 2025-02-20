package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
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
  public String toSourceCode() {
    var calleeSourceCode =
        (callee instanceof SLambda) ? "(" + callee.toSourceCode() + ")" : callee.toSourceCode();
    return calleeSourceCode + "(" + args.elements().map(SExpr::toSourceCode).toString(",") + ")";
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SCall")
        .addField("callee", callee)
        .addField("args", args)
        .addField("location", location)
        .toString();
  }
}
