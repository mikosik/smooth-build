package org.smoothbuild.lang.expr;

import java.math.BigInteger;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.IntTS;

public record IntS(IntTS type, BigInteger bigInteger, Loc loc) implements ExprS {
  @Override
  public String name() {
    return bigInteger.toString();
  }
}
