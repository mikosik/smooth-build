package org.smoothbuild.lang.expr;

import java.util.Optional;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallExpression(Type type, Expression function,
    ImmutableList<Optional<Expression>> arguments, Location location) implements Expression {
}
