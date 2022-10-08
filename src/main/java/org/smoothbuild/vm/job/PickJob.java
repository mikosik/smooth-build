package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.PickTask;

public class PickJob extends ExecutingJob {
  private final PickB pickB;

  public PickJob(PickB pickB, ExecutionContext context) {
    super(context);
    this.pickB = pickB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var data = pickB.data();
    var task = new PickTask(pickB.evalT(), context().labeledLoc(pickB));
    return evaluateTransitively(task, list(data.pickable(), data.index()));
  }
}
