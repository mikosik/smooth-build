package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;

public class ValueReferenceExpression extends Expression {
  private final String name;

  public ValueReferenceExpression(String name, Location location) {
    super(location);
    this.name = name;
  }

  public String name() {
    return name;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
