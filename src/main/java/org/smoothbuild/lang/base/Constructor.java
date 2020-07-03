package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.parse.expr.ConstructorCallExpression;
import org.smoothbuild.parse.expr.Expression;

public class Constructor extends Callable {
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
