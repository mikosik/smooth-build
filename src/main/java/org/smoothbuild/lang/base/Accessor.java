package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.expr.AccessorCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.ConcreteType;

public class Accessor extends Function {
  private final String fieldName;

  public Accessor(Signature signature, String fieldName, Location location) {
    super(signature, location);
    this.fieldName = fieldName;
  }

  public String fieldName() {
    return fieldName;
  }

  @Override
  public Expression createCallExpression(ConcreteType type, Location location) {
    checkArgument(type().equals(type));
    return new AccessorCallExpression(this, location);
  }
}
