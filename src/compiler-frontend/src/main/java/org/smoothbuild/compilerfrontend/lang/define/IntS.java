package org.smoothbuild.compilerfrontend.lang.define;

import static org.smoothbuild.common.collect.List.list;

import java.math.BigInteger;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.IntTS;

public record IntS(IntTS type, BigInteger bigInteger, Location location) implements ConstantS {
  @Override
  public String toString() {
    return "IntS(" + list(type, bigInteger, location).toString(", ") + ")";
  }
}