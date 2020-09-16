package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;

import org.smoothbuild.lang.expr.CallExpression;
import org.smoothbuild.lang.expr.Expression;

import com.google.common.collect.ImmutableList;

/**
 * This class is immutable.
 */
public class Function extends Callable {
  private final Optional<Expression> body;

  public Function(Signature signature, Optional<Expression> body, Location location) {
    super(signature, location);
    this.body = checkNotNull(body);
  }

  public Optional<Expression> body() {
    return body;
  }

  @Override
  public Expression createCallExpression(ImmutableList<Expression> arguments, Location location) {
    return new CallExpression(this, arguments, location);
  }
}
