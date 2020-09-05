package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Function;
import org.smoothbuild.lang.base.Location;

public class FunctionCallExpression extends Expression {
  private final Function function;

  public FunctionCallExpression(Function function, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.function = function;
  }

  public Function function() {
    return function;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
