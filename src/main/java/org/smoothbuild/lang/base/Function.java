package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Optional;

import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

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
  public Expression createCallExpression(ImmutableList<Expression> arguments, Location location) {
    return new CallExpression(this, arguments, location);
  }
}
