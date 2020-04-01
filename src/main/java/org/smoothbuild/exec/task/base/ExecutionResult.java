package org.smoothbuild.exec.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;

/**
 * This class is immutable.
 */
public class ExecutionResult {
  private final TaskResult taskResult;
  private final Throwable throwable;

  public ExecutionResult(TaskResult taskResult) {
    this.taskResult = checkNotNull(taskResult);
    this.throwable = null;
  }

  public ExecutionResult(Throwable throwable) {
    this.taskResult = null;
    this.throwable = checkNotNull(throwable);
  }

  public void apply(
      Consumer<TaskResult> taskResultConsumer,
      Consumer<Throwable> throwableConsumer) {
    if (taskResult != null) {
      taskResultConsumer.accept(taskResult);
    } else {
      throwableConsumer.accept(throwable);
    }
  }
}
