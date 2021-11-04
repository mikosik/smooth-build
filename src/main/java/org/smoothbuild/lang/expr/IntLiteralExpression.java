package org.smoothbuild.lang.expr;

import java.math.BigInteger;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.IntTypeS;

public record IntLiteralExpression(IntTypeS type, BigInteger bigInteger, Location location)
    implements Expression {
}
