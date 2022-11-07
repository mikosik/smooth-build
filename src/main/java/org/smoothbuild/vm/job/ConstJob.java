package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.ConstTask;

public class ConstJob extends Job {
  public ConstJob(InstB instB, ExecutionContext context) {
    super(instB, context);
  }

  @Override
  public InstB exprB() {
    return ((InstB) super.exprB());
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var task = new ConstTask(exprB(), context().trace());
    return evaluateTransitively(task, list());
  }
}
