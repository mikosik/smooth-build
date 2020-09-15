package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;

public record ParameterReferenceExpression(String name, Location location) implements Expression {
  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
