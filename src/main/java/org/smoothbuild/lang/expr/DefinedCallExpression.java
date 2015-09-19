package org.smoothbuild.lang.expr;

import static java.util.Arrays.asList;
import static org.smoothbuild.task.work.TaskWorker.virtualWorker;

import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;

public class DefinedCallExpression extends Expression {
  private final DefinedFunction function;

  public DefinedCallExpression(DefinedFunction function, CodeLocation codeLocation) {
    super(function.type(), asList(function.root()), codeLocation);
    this.function = function;
  }

  @Override
  public TaskWorker createWorker() {
    return virtualWorker(function, codeLocation());
  }
}
