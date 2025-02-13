package org.smoothbuild.compilerfrontend.lang.define;

import java.math.BigInteger;
import org.smoothbuild.common.base.ToStringBuilder;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SIntType;
import org.smoothbuild.compilerfrontend.lang.type.STypeVarSet;

public record SInt(SIntType type, BigInteger bigInteger, Location location) implements SConstant {
  @Override
  public String toSourceCode(STypeVarSet localTypeVars) {
    return bigInteger.toString();
  }

  @Override
  public String toString() {
    return new ToStringBuilder("SInt")
        .addField("type", type)
        .addField("bigInteger", bigInteger)
        .addField("location", location)
        .toString();
  }
}
