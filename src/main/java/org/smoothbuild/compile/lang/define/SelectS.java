package org.smoothbuild.compile.lang.define;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

public record SelectS(TypeS type, ExprS selectable, String field, Loc loc) implements OperS {
  @Override
  public String label() {
    return "." + field;
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return new SelectS(type().mapVars(mapper), selectable.mapVars(mapper), field, loc);
  }
}
