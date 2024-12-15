package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.Name;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public record SSelect(SExpr selectable, Name field, Location location) implements SExpr {
  public SSelect {
    checkArgument(selectable.evaluationType() instanceof SStructType);
  }

  @Override
  public SType evaluationType() {
    var sStructType = (SStructType) selectable.evaluationType();
    return sStructType.fields().get(field).type();
  }

  @Override
  public String toString() {
    var fields = list("selectable = " + selectable, "field = " + field, "location = " + location)
        .toString("\n");
    return "SSelect(\n" + indent(fields) + "\n)";
  }
}
