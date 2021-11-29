package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record SelectS(TypeS type, ExprS structExpr, int index, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "." + index;
  }
}
