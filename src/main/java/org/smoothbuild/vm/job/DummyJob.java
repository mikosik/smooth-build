package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.execute.ExecutionReporter;
import org.smoothbuild.vm.execute.TaskInfo;

import com.google.common.collect.ImmutableList;

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
            reporter.print(taskInfo, list());
          }
          return v;
        });
  }

  protected abstract Promise<InstB> resultPromise();

  protected void report(TaskInfo taskInfo, ImmutableList<Log> logs) {
    reporter.print(taskInfo, logs);
  }
}
