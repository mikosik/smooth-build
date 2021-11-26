package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record SelectS(TypeS type, ExprS structExpr, int index, Location location) implements ExprS {
  @Override
  public String name() {
    return "." + index;
  }
}
