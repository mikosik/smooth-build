package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Item;
import org.smoothbuild.lang.base.Location;

public record FieldReadExpression(int index, Item field, Expression expression, Location location)
    implements Expression {

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
