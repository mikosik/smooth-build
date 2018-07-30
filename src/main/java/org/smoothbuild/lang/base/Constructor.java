package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.lang.expr.ConstructorCallExpression;
import org.smoothbuild.lang.expr.TypeChooser;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;

public class Constructor extends Function {
  public Constructor(Signature signature, Location location) {
    super(signature, location);
  }

  @Override
  public StructType type() {
    return (StructType) signature().type();
  }

  @Override
  public Expression createCallExpression(Type type, TypeChooser evaluatorTypeChooser,
      Location location) {
    checkArgument(type().equals(type));
    return new ConstructorCallExpression(this, location);
  }
}
