package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.compute.ResSource.NOOP;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.execute.ExecutionReporter;
import org.smoothbuild.vm.execute.TaskInfo;

public abstract class DummyJob extends Job {
  private final ExecutionReporter reporter;
  private final TaskInfo taskInfo;

  public DummyJob(TaskInfo taskInfo, ExecutionReporter reporter) {
    this.reporter = reporter;
    this.taskInfo = taskInfo;
  }

  @Override
  public final Promise<InstB> evaluateImpl() {
    return resultPromise()
        .chain(v -> {
          if (v != null) {
            reporter.print(taskInfo, list(), NOOP);
          }
          return v;
        });
  }

  protected abstract Promise<InstB> resultPromise();
}
