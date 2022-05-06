package org.smoothbuild.lang.expr;

import java.math.BigInteger;

import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.lang.type.IntTS;

public record IntS(IntTS type, BigInteger bigInteger, Loc loc) implements ExprS {
  @Override
  public String name() {
    return bigInteger.toString();
  }
}
