package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.util.concurrent.Promise;
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
  protected Promise<ValueB> evaluateImpl() {
    var task = new ConstTask(exprB(), context().trace());
    return evaluateTransitively(task, list());
  }
}
