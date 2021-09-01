package org.smoothbuild.exec.parallel;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResultHandler implements Consumer<Computed> {
  private final Task task;
  private final Consumer<Val> consumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResultHandler(Task task, Consumer<Val> consumer,
      ExecutionReporter reporter, SoftTerminationExecutor jobExecutor) {
    this.task = task;
    this.consumer = consumer;
    this.reporter = reporter;
    this.jobExecutor = jobExecutor;
  }

  @Override
  public void accept(Computed computed) {
    reporter.report(task, computed);
    if (computed.hasOutputWithValue()) {
      consumer.accept(computed.output().value());
    } else {
      jobExecutor.terminate();
    }
  }
}
