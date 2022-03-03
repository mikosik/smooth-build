package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.impl.TypeS;

public record ParamRefS(TypeS type, String paramName, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "(" + paramName + ")";
  }
}
