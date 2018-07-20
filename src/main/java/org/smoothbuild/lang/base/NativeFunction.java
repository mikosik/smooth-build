package org.smoothbuild.lang.base;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.NativeCallExpression;
import org.smoothbuild.lang.type.ConcreteType;

import com.google.common.hash.HashCode;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction extends Function {
  private final Native nativ;
  private final HashCode hash;
  private final boolean isCacheable;

  public NativeFunction(Native nativ, Signature signature, Location location, boolean isCacheable,
      HashCode hash) {
    super(signature, location);
    this.nativ = nativ;
    this.hash = hash;
    this.isCacheable = isCacheable;
  }

  public Native nativ() {
    return nativ;
  }

  public HashCode hash() {
    return hash;
  }

  public boolean isCacheable() {
    return isCacheable;
  }

  @Override
  public Expression createCallExpression(ConcreteType type, Location location) {
    return new NativeCallExpression(type, this, location);
  }
}
