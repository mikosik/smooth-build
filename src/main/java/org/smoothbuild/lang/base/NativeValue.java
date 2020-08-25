package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.NativeValueReferenceExpression;

/**
 * Smooth Value implemented natively in java.
 *
 * @see DefinedValue
 */
public class NativeValue extends Value implements NativeWrapper {
  private final Native nativ;
  private final boolean isCacheable;

  public NativeValue(ConcreteType type, String name, Native nativ, Location location,
      boolean isCacheable) {
    super(type, name, location);
    this.nativ = nativ;
    this.isCacheable = isCacheable;
  }

  @Override
  public Native nativ() {
    return nativ;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  @Override
  public Expression createReferenceExpression(Location location) {
    return new NativeValueReferenceExpression(this, location);
  }
}
