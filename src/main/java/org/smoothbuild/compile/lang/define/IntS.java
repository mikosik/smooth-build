package org.smoothbuild.compile.lang.define;

import static org.smoothbuild.util.collect.Lists.joinToString;

import java.math.BigInteger;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.IntTS;

public record IntS(IntTS type, BigInteger bigInteger, Loc loc) implements ConstantS {
  @Override
  public String toString() {
    return "IntS(" + joinToString(", ", type, bigInteger, loc) + ")";
  }
}
