package org.smoothbuild.lang.obj;

import java.math.BigInteger;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.type.IntTS;

public record IntS(IntTS type, BigInteger bigInteger, Loc loc) implements CnstS {
  @Override
  public String name() {
    return bigInteger.toString();
  }
}
