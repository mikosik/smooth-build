package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.job.TaskKind.CONST;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.lang.base.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class ConstTask extends AbstractJob {
  private final CnstB cnst;
  private final TaskInfo taskInfo;

  public ConstTask(CnstB cnst, Nal nal) {
    super(cnst.type(), nal.loc());
    this.cnst = cnst;
    this.taskInfo = new TaskInfo(CONST, nal);
  }

  @Override
  public Promise<CnstB> scheduleImpl(Worker worker) {
    PromisedValue<CnstB> result = new PromisedValue<>();
    worker.reporter().print(taskInfo, list());
    result.accept(cnst);
    return result;
  }
}
