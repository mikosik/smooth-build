package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.task.base.Task;

public class TaskContainerCreator {
  private final TaskExecutor taskExecutor;

  @Inject
  public TaskContainerCreator(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  public <T extends SValue> TaskContainer<T> create(Task<T> task) {
    return new TaskContainer<T>(taskExecutor, task);
  }
}
