package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.ConstTask;

public class ConstJob extends Job {
  private final InstB instB;

  public ConstJob(InstB instB, ExecutionContext context) {
    super(context);
    this.instB = instB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var task = new ConstTask(instB, context().tagLoc(instB), context().trace());
    return evaluateTransitively(task, list());
  }
}
