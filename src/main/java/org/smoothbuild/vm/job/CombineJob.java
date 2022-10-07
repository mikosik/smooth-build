package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.CombineTask;

public class CombineJob extends ExecutingJob {
  private final CombineB combineB;

  public CombineJob(CombineB combineB, ExecutionContext context) {
    super(context);
    this.combineB = combineB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var task = new CombineTask(combineB.evalT(), context().labeledLoc(combineB));
    return evaluateTransitively(task, combineB.items());
  }
}
