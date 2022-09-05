package org.smoothbuild.compile.lang.define;

import java.math.BigInteger;
import java.util.function.Function;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.type.IntTS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

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
