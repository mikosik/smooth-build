package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Callable;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public record CallExpression(
    Type type, Callable callable, ImmutableList<Expression> arguments, Location location)
    implements Expression {
  @Override
  public <C, T> T visit(C context, ExpressionVisitor<C, T> visitor)
      throws ExpressionVisitorException {
    return visitor.visit(context, this);
  }
}
