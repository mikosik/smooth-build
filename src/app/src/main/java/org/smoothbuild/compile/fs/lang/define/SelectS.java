package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.StructTS;
import org.smoothbuild.compile.fs.lang.type.TypeS;

public record SelectS(ExprS selectable, String field, Location location) implements ExprS {
  public SelectS {
    checkArgument(selectable.evaluationT() instanceof StructTS);
  }

  @Override
  public TypeS evaluationT() {
    var structTS = (StructTS) selectable.evaluationT();
    return structTS.fields().get(field).type();
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "selectable = " + selectable,
        "field = " + field,
        "location = " + location
    );
    return "SelectS(\n" + indent(fields) + "\n)";
  }
}
