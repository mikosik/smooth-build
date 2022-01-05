package org.smoothbuild.vm.job.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.job.job.JobKind.CONST;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class ConstJob extends AbstractJob {
  private final ValB val;
  private final JobInfo jobInfo;

  public ConstJob(ValB val, Nal nal) {
    super(val.type(), nal.loc());
    this.val = val;
    this.jobInfo = new JobInfo(CONST, nal);
  }

  @Override
  public Promise<ValB> scheduleImpl(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    worker.reporter().print(jobInfo, list());
    result.accept(val);
    return result;
  }
}
