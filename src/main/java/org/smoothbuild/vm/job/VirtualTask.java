package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class VirtualTask extends AbstractJob {
  private final Job job;
  private final TaskInfo taskInfo;

  public VirtualTask(Job job, TaskInfo taskInfo) {
    super(job.type(), taskInfo.loc());
    this.job = job;
    this.taskInfo = taskInfo;
  }

  @Override
  public Promise<CnstB> scheduleImpl(Worker worker) {
    PromisedValue<CnstB> result = new PromisedValue<>();
    job
        .schedule(worker)
        .addConsumer(val -> onCompleted(val, worker, result));
    return result;
  }

  private void onCompleted(CnstB cnst, Worker worker, Consumer<CnstB> result) {
    worker.reporter().print(taskInfo, list());
    result.accept(cnst);
  }
}
