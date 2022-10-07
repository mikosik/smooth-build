package org.smoothbuild.compile.lang.define;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;

public record SelectS(ExprS selectable, String field, Loc loc) implements OperS {
  public SelectS {
    checkArgument(selectable.evalT() instanceof StructTS);
  }

  @Override
  public String label() {
    return "." + field;
  }

  @Override
  public TypeS evalT() {
    var structTS = (StructTS) selectable.evalT();
    return structTS.fields().get(field).type();
  }
}
