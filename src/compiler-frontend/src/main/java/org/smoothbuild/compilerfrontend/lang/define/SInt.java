package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

import java.math.BigInteger;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SIntType;

public record SInt(SIntType type, BigInteger bigInteger, Location location) implements SConstant {
  @Override
  public String toString() {
    return "SInt(" + list(type, bigInteger, location).toString(", ") + ")";
  }
}
