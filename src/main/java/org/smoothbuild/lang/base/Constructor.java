package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.lang.expr.ConstructorCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.object.type.StructType;

public class Constructor extends Function {
  public Constructor(Signature signature, Location location) {
    super(signature, location);
  }

  @Override
  public StructType type() {
    return (StructType) signature().type();
  }

  @Override
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new ConstructorCallExpression(this, arguments, location);
  }
}
