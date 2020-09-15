package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ArrayType;

import com.google.common.collect.ImmutableList;

public record ArrayLiteralExpression(
    ArrayType arrayType, ImmutableList<Expression> elements, Location location)
    implements Expression {

  public ArrayType arrayType() {
    return arrayType;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "ArrayLiteralExpression{" + arrayType.name() + ", " + elements + ", " + location() + "}";
  }
}
