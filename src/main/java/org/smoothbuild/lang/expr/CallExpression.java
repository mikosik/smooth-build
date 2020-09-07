package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Callable;
import org.smoothbuild.lang.base.Location;

public class CallExpression extends Expression {
  private final Callable callable;

  public CallExpression(
      Callable callable, List<? extends Expression> arguments, Location location) {
    super(arguments, location);
    this.callable = callable;
  }

  public Callable callable() {
    return callable;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
