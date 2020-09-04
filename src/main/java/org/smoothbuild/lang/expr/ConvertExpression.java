package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ConcreteType;

import com.google.common.collect.ImmutableList;

public class ConvertExpression extends Expression {
  private final ConcreteType type;

  public ConvertExpression(ConcreteType type, Expression expression, Location location) {
    super(ImmutableList.of(expression), location);
    this.type = type;
  }

  public ConcreteType type() {
    return type;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
