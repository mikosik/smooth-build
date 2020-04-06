package org.smoothbuild.exec.task.parallel;

import java.util.function.Consumer;

import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.task.base.MaybeComputed;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

public class ResultHandler implements Consumer<MaybeComputed> {
  private final Task task;
  private final Consumer<SObject> sObjectConsumer;
  private final ExecutionReporter reporter;
  private final SoftTerminationExecutor jobExecutor;

  public ResultHandler(Task task, Consumer<SObject> sObjectConsumer,
      ExecutionReporter reporter, SoftTerminationExecutor jobExecutor) {
    this.task = task;
    this.sObjectConsumer = sObjectConsumer;
    this.reporter = reporter;
    this.jobExecutor = jobExecutor;
  }

  @Override
  public void accept(MaybeComputed maybeComputed) {
    if (maybeComputed.hasComputed()) {
      MaybeOutput result = maybeComputed.computed();
      reporter.report(task, maybeComputed, maybeComputed.isFromCache());
      if (!result.hasOutputWithValue()) {
        jobExecutor.terminate();
      } else {
        sObjectConsumer.accept(result.output().value());
      }
    } else {
      Throwable throwable = maybeComputed.throwable();
      reporter.report(throwable);
      jobExecutor.terminate();
    }
  }

  public void handleComputerException(Throwable e) {
    reporter.report(e);
    jobExecutor.terminate();
  }
}
