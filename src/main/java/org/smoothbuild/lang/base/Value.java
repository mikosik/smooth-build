package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.expr.Expression;

public abstract class Value extends Evaluable {
  public Value(ConcreteType type, String name, Location location) {
    super(type, name, location);
  }

  @Override
  public ConcreteType type() {
    return (ConcreteType) super.type();
  }

  public abstract Expression createReferenceExpression(Location location);
}


