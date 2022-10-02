package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;

public record SelectS(TypeS type, ExprS selectable, String field, Loc loc) implements OperS {
  @Override
  public String label() {
    return "." + field;
  }
}
