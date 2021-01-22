package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.ArrayType;

import com.google.common.collect.ImmutableList;

public record ArrayLiteralExpression(
    ArrayType type, ImmutableList<Expression> elements, Location location)
    implements Expression {

  @Override
  public <C, T> T visit(C context, ExpressionVisitor<C, T> visitor)
      throws ExpressionVisitorException {
    return visitor.visit(context, this);
  }

  @Override
  public String toString() {
    return "ArrayLiteralExpression{" + type.name() + ", " + elements + ", " + location() + "}";
  }
}
