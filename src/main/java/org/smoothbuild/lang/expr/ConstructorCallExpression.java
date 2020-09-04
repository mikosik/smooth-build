package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Location;

public class ConstructorCallExpression extends Expression {
  private final Constructor constructor;

  public ConstructorCallExpression(Constructor constructor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.constructor = constructor;
  }

  public Constructor constructor() {
    return constructor;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) throws ExpressionVisitorException {
    return visitor.visit(this);
  }
}
