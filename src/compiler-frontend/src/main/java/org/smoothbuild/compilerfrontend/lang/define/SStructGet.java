package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Name;
import org.smoothbuild.compilerfrontend.lang.type.SStructType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public record SStructGet(SExpr structExpr, Name field, Location location) implements SExpr {
  public SStructGet {
    checkArgument(structExpr.evaluationType() instanceof SStructType);
  }

  @Override
  public SType evaluationType() {
    var sStructType = (SStructType) structExpr.evaluationType();
    return requireNonNull(sStructType.fields().get(field)).type();
  }

  @Override
  public String toSourceCode() {
    return structExpr.toSourceCode() + "." + field;
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SStructGet")
        .addField("structExpr", structExpr)
        .addField("field", field)
        .addField("location", location)
        .toString();
  }
}
