package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.LITERAL;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class ConstJob extends AbstractJob {
  private final ValB val;

  public ConstJob(ValB val, Nal nal) {
    super(val.type(), list(), nal);
    this.val = val;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    worker.reporter().print(new TaskInfo(LITERAL, this), list());
    result.accept(val);
    return result;
  }
}
