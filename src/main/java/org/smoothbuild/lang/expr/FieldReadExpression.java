package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ItemSignature;
import org.smoothbuild.lang.base.type.Type;

public record FieldReadExpression(ItemSignature field, Expression expression, Location location)
    implements Expression {
  @Override
  public Type type() {
    return field.type();
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
