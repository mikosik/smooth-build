package org.smoothbuild.vm.job.job;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class VirtualJob extends AbstractJob {
  private final Job job;
  private final JobInfo jobInfo;

  public VirtualJob(Job job, JobInfo jobInfo) {
    super(job.type(), jobInfo.loc());
    this.job = job;
    this.jobInfo = jobInfo;
  }

  @Override
  public Promise<ValB> scheduleImpl(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    job
        .schedule(worker)
        .addConsumer(val -> onCompleted(val, worker, result));
    return result;
  }

  private void onCompleted(ValB val, Worker worker, Consumer<ValB> result) {
    worker.reporter().print(jobInfo, list());
    result.accept(val);
  }
}
