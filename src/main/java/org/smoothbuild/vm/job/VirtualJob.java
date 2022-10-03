package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.execute.ExecutionReporter;
import org.smoothbuild.vm.execute.TaskInfo;

public class VirtualJob extends DummyJob {
  private final Job job;

  public VirtualJob(Job job, TaskInfo taskInfo, ExecutionReporter reporter) {
    super(taskInfo, reporter);
    this.job = job;
  }

  @Override
  protected Promise<InstB> resultPromise() {
    return job.evaluate();
  }
}
