package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class VirtualJob extends AbstractJob {
  private final TaskInfo taskInfo;

  public VirtualJob(Job job, TaskInfo taskInfo) {
    super(job.type(), list(job), taskInfo);
    this.taskInfo = taskInfo;
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    PromisedValue<ValueH> result = new PromisedValue<>();
    wrappedJob()
        .schedule(worker)
        .addConsumer(val -> onCompleted(val, worker, result));
    return result;
  }

  private void onCompleted(ValueH val, Worker worker, Consumer<ValueH> result) {
    worker.reporter().print(taskInfo, list());
    result.accept(val);
  }

  private Job wrappedJob() {
    return deps().get(0);
  }
}
