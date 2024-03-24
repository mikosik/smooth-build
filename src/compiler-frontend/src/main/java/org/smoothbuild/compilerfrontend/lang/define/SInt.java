package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

import java.math.BigInteger;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SIntType;

public record SInt(SIntType type, BigInteger bigInteger, Location location) implements SConstant {
  @Override
  public String toString() {
    return "IntS(" + list(type, bigInteger, location).toString(", ") + ")";
  }
}
