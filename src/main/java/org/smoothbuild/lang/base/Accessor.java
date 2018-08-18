package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.lang.expr.AccessorCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.ConcreteType;

public class Accessor extends Function {
  private final String fieldName;

  public Accessor(Signature signature, String fieldName, Location location) {
    super(signature, location);
    this.fieldName = fieldName;
  }

  @Override
  public ConcreteType type() {
    return (ConcreteType) signature().type();
  }

  public String fieldName() {
    return fieldName;
  }

  @Override
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new AccessorCallExpression(this, arguments, location);
  }
}
