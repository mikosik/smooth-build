package org.smoothbuild.lang.base;

import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.NativeValueReferenceExpression;

/**
 * Smooth Value implemented natively in java.
 *
 * @see DefinedValue
 */
public class NativeValue extends Value {
  public NativeValue(ConcreteType type, String name, Location location) {
    super(type, name, location);
  }

  @Override
  public Expression createReferenceExpression(Location location) {
    return new NativeValueReferenceExpression(this, location);
  }
}
