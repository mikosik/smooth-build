package org.smoothbuild.lang.obj;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;

public record ParamRefS(TypeS type, String paramName, Loc loc) implements ExprS {
  @Override
  public String name() {
    return "(" + paramName + ")";
  }
}
