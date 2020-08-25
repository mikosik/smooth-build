package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.NativeCallExpression;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction extends Callable implements NativeWrapper {
  private final Native nativ;
  private final boolean isCacheable;

  public NativeFunction(Native nativ, Signature signature, Location location, boolean isCacheable) {
    super(signature, location);
    this.nativ = nativ;
    this.isCacheable = isCacheable;
  }

  @Override
  public String extendedName() {
    return nameWithParentheses();
  }

  @Override
  public Native nativ() {
    return nativ;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  @Override
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new NativeCallExpression(this, arguments, location);
  }
}
