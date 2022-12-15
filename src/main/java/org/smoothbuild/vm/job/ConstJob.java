package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.task.ConstTask;

public class ConstJob extends Job {
  public ConstJob(ValueB valueB, ExecutionContext context) {
    super(valueB, context);
  }

  @Override
  public ValueB exprB() {
    return ((ValueB) super.exprB());
  }

  @Override
  protected void evaluateImpl(Consumer<ValueB> result) {
    var task = new ConstTask(exprB(), context().trace());
    evaluateTransitively(task, list(), result);
  }
}
