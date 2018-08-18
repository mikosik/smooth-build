package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.NativeCallExpression;

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
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new NativeCallExpression(this, arguments, location);
  }
}
