package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.MonoTS;

public record SelectS(MonoTS type, MonoObjS selectable, String field, Loc loc) implements MonoExprS {
  @Override
  public String name() {
    return "." + field;
  }
}
