package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Location;

public class AccessorCallExpression extends Expression {
  private final Accessor accessor;

  public AccessorCallExpression(Accessor accessor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.accessor = accessor;
  }

  public Accessor accessor() {
    return accessor;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
