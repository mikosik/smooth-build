package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Location;

public class DefinedCallExpression extends Expression {
  private final DefinedFunction function;

  public DefinedCallExpression(DefinedFunction definedFunction,
      List<? extends Expression> arguments, Location location) {
    super(arguments, location);
    this.function = definedFunction;
  }

  public DefinedFunction function() {
    return function;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
