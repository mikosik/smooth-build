package org.smoothbuild.exec.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;

/**
 * This class is immutable.
 */
public class ExecutionResult {
  private final Result result;
  private final Throwable throwable;

  public ExecutionResult(Result result) {
    this.result = checkNotNull(result);
    this.throwable = null;
  }

  public ExecutionResult(Throwable throwable) {
    this.result = null;
    this.throwable = checkNotNull(throwable);
  }

  public void apply(
      Consumer<Result> taskResultConsumer,
      Consumer<Throwable> throwableConsumer) {
    if (result != null) {
      taskResultConsumer.accept(result);
    } else {
      throwableConsumer.accept(throwable);
    }
  }
}
