package org.smoothbuild.lang.define;

import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

public record SelectS(TypeS type, ExprS selectable, String field, Loc loc) implements OperS {
  @Override
  public String name() {
    return "." + field;
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new SelectS(type().mapVars(mapper), selectable.mapVars(mapper), field, loc);
  }
}
