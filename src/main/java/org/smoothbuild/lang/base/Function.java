package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.FunctionCallExpression;

/**
 * Smooth function declared in a smooth file.
 */
public class Function extends Callable {
  private final Optional<Expression> body;

  public Function(Signature signature, Location location, Optional<Expression> body) {
    super(signature, location);
    this.body = checkNotNull(body);
  }

  @Override
  public String extendedName() {
    return nameWithParentheses();
  }

  public Optional<Expression> body() {
    return body;
  }

  @Override
  public Expression createCallExpression(List<? extends Expression> arguments, Location location) {
    return new FunctionCallExpression(this, arguments, location);
  }
}
