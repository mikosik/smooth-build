package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

public class VirtualJob extends AbstractJob {
  private final TaskInfo taskInfo;

  public VirtualJob(Job job, TaskInfo taskInfo) {
    super(job.type(), list(job), taskInfo);
    this.taskInfo = taskInfo;
  }

  @Override
  public Feeder<Val> schedule(Worker worker) {
    FeedingConsumer<Val> result = new FeedingConsumer<>();
    wrappedJob()
        .schedule(worker)
        .addConsumer(val -> onCompleted(val, worker, result));
    return result;
  }

  private void onCompleted(Val val, Worker worker, Consumer<Val> result) {
    worker.reporter().print(taskInfo, list());
    result.accept(val);
  }

  private Job wrappedJob() {
    return dependencies().get(0);
  }
}
