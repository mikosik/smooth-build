package org.smoothbuild.vm.job.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class ValJob extends AbstractJob {
  private final ValB val;

  public ValJob(ValB val, Nal nal) {
    super(val.type(), list(), nal);
    this.val = val;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    worker.reporter().print(new TaskInfo(TaskKind.LITERAL, this), list());
    result.accept(val);
    return result;
  }
}
