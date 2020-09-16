package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Constructor extends Callable {
  public Constructor(Signature signature, Location location) {
    super(signature, location);
  }

  @Override
  public StructType type() {
    return (StructType) signature().type();
  }

  @Override
  public Expression createCallExpression(ImmutableList<Expression> arguments, Location location) {
    return new CallExpression(this, arguments, location);
  }
}
