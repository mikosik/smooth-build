package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.smoothbuild.parse.expr.DefinedCallExpression;
import org.smoothbuild.parse.expr.Expression;

/**
 * Smooth function defined in smooth language via smooth expression.
 *
 * @see NativeFunction
 */
public class DefinedFunction extends Function {
  private final Expression body;

  public DefinedFunction(Signature signature, Location location, Expression body) {
    super(signature, location);
    this.body = checkNotNull(body);
  }

  public Expression body() {
    return body;
  }

  @Override
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new DefinedCallExpression(this, arguments, location);
  }
}
