package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public record ParameterReferenceExpression(Type type, String name, Location location)
    implements Expression {
  @Override
  public <C, T> T visit(C context, ExpressionVisitor<C, T> visitor)
      throws ExpressionVisitorException {
    return visitor.visit(context, this);
  }
}
