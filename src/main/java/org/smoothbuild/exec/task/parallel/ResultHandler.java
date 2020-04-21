package org.smoothbuild.exec.task.parallel;

import java.util.function.Consumer;

import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.exec.task.base.Computed;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResultHandler implements Consumer<Computed> {
  private final BuildTask task;
  private final Consumer<SObject> consumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResultHandler(BuildTask task, Consumer<SObject> consumer,
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
