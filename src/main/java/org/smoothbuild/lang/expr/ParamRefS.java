package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record ParamRefS(TypeS type, String paramName, Location location) implements ExprS {
  @Override
  public String name() {
    return "(" + paramName + ")";
  }
}
