package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;

public record StringLiteralExpression(String string, Location location) implements Expression {

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "StringLiteralExpression{" + string + ", " + location() + "}";
  }
}
