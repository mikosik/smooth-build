package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.job.TaskKind.CONST;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class ConstTask extends AbstractJob {
  private final ValB val;
  private final TaskInfo taskInfo;

  public ConstTask(ValB val, Nal nal) {
    super(val.type(), nal.loc());
    this.val = val;
    this.taskInfo = new TaskInfo(CONST, nal);
  }

  @Override
  public Promise<ValB> scheduleImpl(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    worker.reporter().print(taskInfo, list());
    result.accept(val);
    return result;
  }
}
