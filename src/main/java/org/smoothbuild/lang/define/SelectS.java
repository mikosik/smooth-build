package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;

public record SelectS(TypeS type, MonoObjS selectable, String field, Loc loc) implements MonoExprS {
  @Override
  public String name() {
    return "." + field;
  }
}
