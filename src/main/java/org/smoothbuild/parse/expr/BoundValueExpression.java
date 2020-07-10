package org.smoothbuild.parse.expr;

import org.smoothbuild.lang.base.Location;

public class BoundValueExpression extends Expression {
  private final String name;

  public BoundValueExpression(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
