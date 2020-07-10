package org.smoothbuild.parse.expr;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.NativeFunction;

public class NativeCallExpression extends Expression {
  private final NativeFunction nativeFunction;

  public NativeCallExpression(NativeFunction nativeFunction, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.nativeFunction = nativeFunction;
  }

  public NativeFunction nativeFunction() {
    return nativeFunction;
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
