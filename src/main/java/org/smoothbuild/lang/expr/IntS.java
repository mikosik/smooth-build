package org.smoothbuild.lang.expr;

import java.math.BigInteger;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.IntTypeS;

public record IntS(IntTypeS type, BigInteger bigInteger, Loc loc) implements ExprS {
  @Override
  public String name() {
    return bigInteger.toString();
  }
}
