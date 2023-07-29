package org.smoothbuild.compile.fs.lang.define;

import static org.smoothbuild.common.collect.Iterables.joinToString;

import java.math.BigInteger;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.IntTS;

public record IntS(IntTS type, BigInteger bigInteger, Location location) implements ConstantS {
  @Override
  public String toString() {
    return "IntS(" + joinToString(", ", type, bigInteger, location) + ")";
  }
}
