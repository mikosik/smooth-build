package org.smoothbuild.vm.evaluate.job;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.task.ConstTask;

public class ConstJob extends Job {
  public ConstJob(ValueB valueB, JobCreator jobCreator) {
    super(valueB, jobCreator);
  }

  @Override
  public ValueB exprB() {
    return ((ValueB) super.exprB());
  }

  @Override
  protected void evaluateImpl(ExecutionContext context, Consumer<ValueB> result) {
    var task = new ConstTask(exprB(), jobCreator().trace());
    evaluateTransitively(context, task, list(), result);
  }
}
