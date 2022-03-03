package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.impl.TypeS;

public record SelectS(TypeS type, ExprS selectable, String field, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "." + field;
  }
}
