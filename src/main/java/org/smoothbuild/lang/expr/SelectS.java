package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record SelectS(TypeS type, ExprS selectable, String field, Loc loc) implements ExprS {
  public SelectS {
    checkArgument(!type.hasOpenVars());
  }

  @Override
  public String name() {
    return "." + field;
  }
}
