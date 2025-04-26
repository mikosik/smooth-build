package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Name;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public record SStructSelect(SExpr selectable, Name field, Location location) implements SExpr {
  public SStructSelect {
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
    return new ToStringBuilder("SStructSelect")
        .addField("selectable", selectable)
        .addField("field", field)
        .addField("location", location)
        .toString();
  }
}
