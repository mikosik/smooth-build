package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Location;

import com.google.common.collect.ImmutableList;

public record CallExpression(
    Callable callable, ImmutableList<Expression> arguments, Location location)
    implements Expression {
  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
