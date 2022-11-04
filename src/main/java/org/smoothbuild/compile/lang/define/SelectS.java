package org.smoothbuild.compile.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.joinToString;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TypeS;

public record SelectS(ExprS selectable, String field, Loc loc) implements OperS {
  public SelectS {
    checkArgument(selectable.evalT() instanceof StructTS);
  }

  @Override
  public TypeS evalT() {
    var structTS = (StructTS) selectable.evalT();
    return structTS.fields().get(field).type();
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "selectable = " + selectable,
        "field = " + field,
        "loc = " + loc
    );
    return "SelectS(\n" + indent(fields) + "\n)";
  }
}
