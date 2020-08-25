package org.smoothbuild.lang.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.BoundValueExpression;
import org.smoothbuild.lang.expr.Expression;

public class Value extends Evaluable {
  private final Expression body;

  public Value(Type type, String name, Expression body, Location location) {
    super(type, name, location);
    this.body = checkNotNull(body);
  }

  public Expression body() {
    return body;
  }

  public Expression createArglessEvaluationExpression(Location location) {
    return new BoundValueExpression(name(), location);
  }
}


