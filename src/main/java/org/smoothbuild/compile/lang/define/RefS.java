package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;

public record RefS(TypeS evalT, String paramName, Loc loc) implements OperS {
  @Override
  public String label() {
    return "(" + paramName + ")";
  }
}
