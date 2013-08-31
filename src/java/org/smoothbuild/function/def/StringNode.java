package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.function.expr.LiteralExpression.stringExpression;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;
import org.smoothbuild.task.PrecalculatedTask;
import org.smoothbuild.task.Task;

public class StringNode implements DefinitionNode {
  private final String string;

  public StringNode(String string) {
    this.string = checkNotNull(string);
  }

  @Override
  public Type type() {
    return Type.STRING;
  }

  @Override
  public Expression expression(ExpressionIdFactory idFactory) {
    return stringExpression(idFactory.createId(string), string);
  }

  @Override
  public Task generateTask() {
    return new PrecalculatedTask(string);
  }
}
