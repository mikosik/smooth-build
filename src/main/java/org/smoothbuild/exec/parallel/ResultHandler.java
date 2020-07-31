package org.smoothbuild.exec.parallel;

import java.util.function.Consumer;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.exec.base.MaybeOutput;
import org.smoothbuild.exec.compute.Computed;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResultHandler implements Consumer<Computed> {
  private final Task task;
  private final Consumer<Record> consumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResultHandler(Task task, Consumer<Record> consumer,
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
