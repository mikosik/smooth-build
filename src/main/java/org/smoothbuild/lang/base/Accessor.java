package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.expr.Expression;

public class Accessor extends Evaluable {
  private final int fieldIndex;

  public Accessor(ConcreteType type, String name, int fieldIndex, Location location) {
    super(type, name, location);
    this.fieldIndex = fieldIndex;
  }

  @Override
  public ConcreteType type() {
    return (ConcreteType) super.type();
  }

  @Override
  public String extendedName() {
    return "." + name();
  }

  @Override
  public Expression createArglessEvaluationExpression(Location location) {
    throw new UnsupportedOperationException();
  }

  public int fieldIndex() {
    return fieldIndex;
  }
}
