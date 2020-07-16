package org.smoothbuild.parse.expr;

import org.smoothbuild.lang.base.Location;

public class StringLiteralExpression extends Expression {
  private final String string;

  public StringLiteralExpression(String string, Location location) {
    super(location);
    this.string = string;
  }

  public String string() {
    return string;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
