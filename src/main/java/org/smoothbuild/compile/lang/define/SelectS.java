package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;

public record SelectS(ExprS selectable, String field, Loc loc) implements OperS {
  @Override
  public String label() {
    return "." + field;
  }

  @Override
  public TypeS type() {
    var structTS = (StructTS) selectable.type();
    return structTS.fields().get(field).type();
  }
}
