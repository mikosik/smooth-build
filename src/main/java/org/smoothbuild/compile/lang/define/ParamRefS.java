package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;

public record ParamRefS(TypeS evalT, String paramName, Loc loc) implements ExprS {
  @Override
  public String toString() {
    return "ParamRefS(" + joinToString(", ", evalT, paramName, loc) + ")";
  }
}
