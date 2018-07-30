package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.expr.AccessorCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.TypeChooser;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Type;

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
  public Expression createCallExpression(Type type, TypeChooser<ConcreteType> evaluatorTypeChooser,
      Location location) {
    checkArgument(type().equals(type));
    return new AccessorCallExpression(this, location);
  }
}
