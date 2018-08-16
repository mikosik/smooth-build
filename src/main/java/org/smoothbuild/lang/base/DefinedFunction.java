package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.expr.DefinedCallExpression;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.util.Dag;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
 */
public class DefinedFunction extends Function {
  private final Dag<Expression> body;

  public DefinedFunction(Signature signature, Location location, Dag<Expression> body) {
    super(signature, location);
    this.body = checkNotNull(body);
  }

  public Dag<Expression> body() {
    return body;
  }

  @Override
  public Expression createCallExpression(Location location) {
    return new DefinedCallExpression(this, location);
  }
}
