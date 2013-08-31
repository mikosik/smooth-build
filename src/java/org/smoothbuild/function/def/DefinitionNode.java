package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.expr.Expression;
import org.smoothbuild.function.expr.ExpressionIdFactory;
import org.smoothbuild.task.Task;

public interface DefinitionNode {
  public Type type();

  public Expression expression(ExpressionIdFactory idFactory);

  public Task generateTask();
}
