package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record SelectS(TypeS type, ExprS structExpr, String field, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "." + field;
  }
}
