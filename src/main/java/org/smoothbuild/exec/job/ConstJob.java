package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.LITERAL;
import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class ConstJob extends AbstractJob {
  private final ValH val;

  public ConstJob(ValH val, Nal nal) {
    super(val.type(), list(), nal);
    this.val = val;
  }

  @Override
  public Promise<ValH> schedule(Worker worker) {
    PromisedValue<ValH> result = new PromisedValue<>();
    worker.reporter().print(new TaskInfo(LITERAL, this), list());
    result.accept(val);
    return result;
  }
}
