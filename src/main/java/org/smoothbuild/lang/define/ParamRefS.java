package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.MonoTS;

public record ParamRefS(MonoTS type, String paramName, Loc loc) implements MonoExprS {
  @Override
  public String name() {
    return "(" + paramName + ")";
  }
}
