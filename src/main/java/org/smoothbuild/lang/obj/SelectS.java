package org.smoothbuild.lang.obj;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;

public record SelectS(TypeS type, ObjS selectable, String field, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "." + field;
  }
}
