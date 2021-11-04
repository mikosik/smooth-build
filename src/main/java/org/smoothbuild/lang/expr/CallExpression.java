package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallExpression(TypeS type, Expression function,
    ImmutableList<Expression> arguments, Location location) implements Expression {
}
