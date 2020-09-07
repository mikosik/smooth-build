package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;

public class Constructor extends Callable {
  public Constructor(Signature signature, Location location) {
    super(signature, location);
  }

  @Override
  public String extendedName() {
    return nameWithParentheses();
  }

  @Override
  public StructType type() {
    return (StructType) signature().type();
  }

  @Override
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new CallExpression(this, arguments, location);
  }
}
