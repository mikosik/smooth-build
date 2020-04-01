package org.smoothbuild.exec.task.parallel;

import java.util.function.Consumer;

import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.exec.task.base.TaskResult;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;

/**
 * This class is thread-safe.
 * Consumers registered with {@link #addValueConsumer(Consumer)} and
 * {@link #addValueAvailableListener(Runnable)} are called without any lock held.
 */
public class Job {
  private final Task task;
  private final Feeder<TaskResult> taskResultFeeder;

  public Job(Task task) {
    this.task = task;
    this.taskResultFeeder = new Feeder<>();
  }

  public Task task() {
    return task;
  }

  public TaskResult taskResult() {
    return taskResultFeeder.value();
  }

  public void addValueAvailableListener(Runnable runnable) {
    addValueConsumer((value) -> runnable.run());
  }

  public void addValueConsumer(Consumer<SObject> valueConsumer) {
    taskResultFeeder.addConsumer((TaskResult taskResult) -> {
      if (taskResult.hasOutputWithValue()) {
        valueConsumer.accept(taskResult.output().value());
      }
    });
  }

  public void setTaskResult(TaskResult taskResult) {
    taskResultFeeder.accept(taskResult);
  }
}
