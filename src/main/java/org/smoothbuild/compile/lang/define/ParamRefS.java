package org.smoothbuild.compile.lang.define;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

public record ParamRefS(TypeS type, String paramName, Loc loc) implements OperS {
  @Override
  public String name() {
    return "(" + paramName + ")";
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new ParamRefS(type.mapVars(mapper), paramName, loc);
  }
}
