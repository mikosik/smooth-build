package org.smoothbuild.exec.parallel;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.TaskInfo;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResultHandler implements Consumer<Computed> {
  private final TaskInfo taskInfo;
  private final Consumer<Val> consumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResultHandler(TaskInfo taskInfo, Consumer<Val> consumer,
      ExecutionReporter reporter, SoftTerminationExecutor jobExecutor) {
    this.taskInfo = taskInfo;
    this.consumer = consumer;
    this.reporter = reporter;
    this.jobExecutor = jobExecutor;
  }

  @Override
  public void accept(Computed computed) {
    reporter.report(taskInfo, computed);
    if (computed.hasOutputWithValue()) {
      consumer.accept(computed.output().value());
    } else {
      jobExecutor.terminate();
    }
  }
}
