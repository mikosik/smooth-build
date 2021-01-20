package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Types;

public record StringLiteralExpression(String string, Location location) implements Expression {
  @Override
  public Type type() {
    return Types.string();
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return "StringLiteralExpression{" + string + ", " + location() + "}";
  }
}
