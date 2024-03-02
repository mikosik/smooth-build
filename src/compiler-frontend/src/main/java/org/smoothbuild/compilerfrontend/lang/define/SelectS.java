package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.StructTS;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

public record SelectS(ExprS selectable, String field, Location location) implements ExprS {
  public SelectS {
    checkArgument(selectable.evaluationType() instanceof StructTS);
  }

  @Override
  public TypeS evaluationType() {
    var structTS = (StructTS) selectable.evaluationType();
    return structTS.fields().get(field).type();
  }

  @Override
  public String toString() {
    var fields = list("selectable = " + selectable, "field = " + field, "location = " + location)
        .toString("\n");
    return "SelectS(\n" + indent(fields) + "\n)";
  }
}
