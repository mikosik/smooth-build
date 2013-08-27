package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

public class InvalidNode implements DefinitionNode {
  private final Type type;

  public InvalidNode(Type type) {
    this.type = checkNotNull(type);
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public Expression expression(ExpressionIdFactory idFactory) {
    throw new RuntimeException("InvalidNode.expression() should not be called.");
  }
}
