package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record ParamRefS(TypeS type, String paramName, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "(" + paramName + ")";
  }
}
