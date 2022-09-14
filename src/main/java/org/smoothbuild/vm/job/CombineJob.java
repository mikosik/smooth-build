package org.smoothbuild.vm.job;

import static org.smoothbuild.vm.execute.TaskKind.COMBINE;

import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.execute.TaskInfo;
import org.smoothbuild.vm.task.CombineTask;

public class CombineJob extends ExecutingJob {
  private final CombineB combineB;

  public CombineJob(CombineB combineB, ExecutionContext context) {
    super(context);
    this.combineB = combineB;
  }

  @Override
  protected Promise<ValB> evaluateImpl() {
    var task = new CombineTask(combineB.type());
    var taskInfo = new TaskInfo(COMBINE, context().infoFor(combineB));
    return evaluateTransitively(taskInfo, task, combineB.items());
  }
}
