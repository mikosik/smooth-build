package org.smoothbuild.lang.expr;

import java.math.BigInteger;

import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.impl.IntTypeS;

public record IntS(IntTypeS type, BigInteger bigInteger, Loc loc) implements LiteralS {
  @Override
  public String name() {
    return toShortString();
  }

  @Override
  public String toShortString() {
    return bigInteger.toString();
  }
}
