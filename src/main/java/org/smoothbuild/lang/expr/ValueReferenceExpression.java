package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.Type;

public record ValueReferenceExpression(String name, Type type, Location location) implements Expression {
  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
