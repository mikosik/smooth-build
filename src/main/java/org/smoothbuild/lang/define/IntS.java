package org.smoothbuild.lang.define;

import java.math.BigInteger;
import java.util.function.Function;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.IntTS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;

public record IntS(IntTS type, BigInteger bigInteger, Loc loc) implements ValS {
  @Override
  public String name() {
    return bigInteger.toString();
  }

  @Override
  public ExprS mapVars(Function<VarS, TypeS> mapper) {
    return this;
  }
}
