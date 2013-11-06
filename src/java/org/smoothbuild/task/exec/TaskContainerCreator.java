package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.task.base.Task;

public class TaskContainerCreator {
  private final TaskExecutor taskExecutor;

  @Inject
  public TaskContainerCreator(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  public TaskContainer create(Task task) {
    return new TaskContainer(taskExecutor, task);
  }
}
