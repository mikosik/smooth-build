package org.smoothbuild.lang.base;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.parse.expr.Expression;
import org.smoothbuild.parse.expr.NativeCallExpression;

/**
 * Smooth Function implemented natively in java.
 *
 * @see DefinedFunction
 */
public class NativeFunction extends Function {
  private final Native nativ;
  private final Hash hash;
  private final boolean isCacheable;

  public NativeFunction(Native nativ, Signature signature, Location location, boolean isCacheable,
      Hash hash) {
    super(signature, location);
    this.nativ = nativ;
    this.hash = hash;
    this.isCacheable = isCacheable;
  }

  public Native nativ() {
    return nativ;
  }

  public Hash hash() {
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
