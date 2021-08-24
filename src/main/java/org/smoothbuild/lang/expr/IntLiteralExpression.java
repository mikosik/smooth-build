package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.type.Types.intT;

import java.math.BigInteger;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public record IntLiteralExpression(BigInteger bigInteger, Location location)
    implements Expression {
  @Override
  public Type type() {
    return intT();
  }
}
