package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.parse.expr.AccessorCallExpression;
import org.smoothbuild.parse.expr.Expression;

public class Accessor extends Callable {
  private final int fieldIndex;

  public Accessor(Signature signature, int fieldIndex, Location location) {
    super(signature, location);
    this.fieldIndex = fieldIndex;
  }

  @Override
  public ConcreteType type() {
    return (ConcreteType) signature().type();
  }

  public int fieldIndex() {
    return fieldIndex;
  }

  @Override
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new AccessorCallExpression(this, arguments, location);
  }
}
