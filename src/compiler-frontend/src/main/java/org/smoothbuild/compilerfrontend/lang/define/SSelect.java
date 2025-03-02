package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Name;
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
  public String toSourceCode() {
    return selectable.toSourceCode() + "." + field;
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SSelect")
        .addField("selectable", selectable)
        .addField("field", field)
        .addField("location", location)
        .toString();
  }
}
