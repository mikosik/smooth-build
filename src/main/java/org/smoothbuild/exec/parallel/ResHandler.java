package org.smoothbuild.exec.parallel;

import java.util.function.Consumer;

import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.job.TaskInfo;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResHandler implements Consumer<Computed> {
  private final TaskInfo taskInfo;
  private final Consumer<ValB> consumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResHandler(TaskInfo taskInfo, Consumer<ValB> consumer,
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
      consumer.accept(computed.output().val());
    } else {
      jobExecutor.terminate();
    }
  }
}
