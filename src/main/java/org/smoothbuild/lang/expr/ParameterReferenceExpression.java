package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;

public class ParameterReferenceExpression extends Expression {
  private final String name;

  public ParameterReferenceExpression(String name, Location location) {
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
