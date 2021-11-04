package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.ArraySType;

import com.google.common.collect.ImmutableList;

public record ArrayLiteralExpression(
    ArraySType type, ImmutableList<Expression> elements, Location location)
    implements Expression {
}
