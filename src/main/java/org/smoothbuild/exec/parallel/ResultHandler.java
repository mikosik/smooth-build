package org.smoothbuild.exec.parallel;

import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.base.MaybeOutput;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResultHandler implements Consumer<Computed> {
  private final Task task;
  private final Consumer<Obj> consumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResultHandler(Task task, Consumer<Obj> consumer,
      ExecutionReporter reporter, SoftTerminationExecutor jobExecutor) {
    this.task = task;
    this.consumer = consumer;
    this.reporter = reporter;
    this.jobExecutor = jobExecutor;
  }

  @Override
  public void accept(Computed computed) {
    reporter.report(task, computed);
    MaybeOutput result = computed.computed();
    if (result.hasOutputWithValue()) {
      consumer.accept(result.output().value());
    } else {
      jobExecutor.terminate();
    }
  }
}
