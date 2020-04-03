package org.smoothbuild.exec.task.parallel;

import java.util.function.Consumer;

import org.smoothbuild.exec.task.base.Result;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;

/**
 * This class is thread-safe.
 * Consumers registered with {@link #addValueConsumer(Consumer)} and
 * {@link #addValueAvailableListener(Runnable)} are called without any lock held.
 */
public class Job {
  private final Task task;
  private final Feeder<Result> taskResultFeeder;

  public Job(Task task) {
    this.task = task;
    this.taskResultFeeder = new Feeder<>();
  }

  public Task task() {
    return task;
  }

  public Result taskResult() {
    return taskResultFeeder.value();
  }

  public void addValueAvailableListener(Runnable runnable) {
    addValueConsumer((value) -> runnable.run());
  }

  public void addValueConsumer(Consumer<SObject> valueConsumer) {
    taskResultFeeder.addConsumer((Result result) -> {
      if (result.hasOutputWithValue()) {
        valueConsumer.accept(result.output().value());
      }
    });
  }

  public void setTaskResult(Result result) {
    taskResultFeeder.accept(result);
  }
}
