package org.smoothbuild.lang.expr;

import java.math.BigInteger;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.IntType;

public record IntLiteralExpression(IntType type, BigInteger bigInteger, Location location)
    implements Expression {
}
