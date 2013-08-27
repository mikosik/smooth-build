package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;

public class ExpressionNode implements DefinitionNode {
  private final Expression expression;

  public ExpressionNode(Expression expression) {
    this.expression = checkNotNull(expression);
  }

  @Override
  public Type type() {
    return expression.type();
  }

  @Override
  public Expression expression(ExpressionIdFactory idFactory) {
    return expression;
  }
}
