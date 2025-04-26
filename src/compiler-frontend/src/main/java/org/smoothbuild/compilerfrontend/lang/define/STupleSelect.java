package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.base.Preconditions.checkArgument;
import static java.math.BigInteger.ONE;

import java.math.BigInteger;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.STupleType;
import org.smoothbuild.compilerfrontend.lang.type.SType;

public record STupleSelect(SExpr selectable, BigInteger index, Location location) implements SExpr {
  public STupleSelect {
    checkArgument(selectable.evaluationType() instanceof STupleType);
  }

  @Override
  public SType evaluationType() {
    var sTupleType = (STupleType) selectable.evaluationType();
    return sTupleType.elements().get(index.intValue());
  }

  @Override
  public String toSourceCode() {
    return selectable.toSourceCode() + "." + (index.add(ONE));
  }

  @Override
  public String toString() {
    return new ToStringBuilder("STupleSelect")
        .addField("selectable", selectable)
        .addField("index", index)
        .addField("location", location)
        .toString();
  }
}
