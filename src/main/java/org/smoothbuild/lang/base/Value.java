package org.smoothbuild.lang.base;

import java.util.Optional;

import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ValueReferenceExpression;

/**
 * This class is immutable.
 */
public class Value extends Evaluable {
  private final Optional<Expression> body;

  public Value(ConcreteType type, String name, Optional<Expression> body, Location location) {
    super(type, name, location);
    this.body = body;
  }

  @Override
  public ConcreteType type() {
    return (ConcreteType) super.type();
  }

  public Optional<Expression> body() {
    return body;
  }

  public Expression createReferenceExpression(Location location) {
    return new ValueReferenceExpression(name(), location);
  }
}


