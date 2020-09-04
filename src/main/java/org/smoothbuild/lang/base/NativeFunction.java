package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.NativeCallExpression;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction extends Callable {
  public NativeFunction(Signature signature, Location location) {
    super(signature, location);
  }

  @Override
  public String extendedName() {
    return nameWithParentheses();
  }

  @Override
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new NativeCallExpression(this, arguments, location);
  }
}
