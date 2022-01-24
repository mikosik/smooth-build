package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.TypeS;

public record ParamRefS(TypeS type, String paramName, Loc loc) implements ExprS {
  public ParamRefS {
    checkArgument(!type.hasOpenVars());
  }

  @Override
  public String name() {
    return "(" + paramName + ")";
  }
}
