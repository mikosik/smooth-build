package org.smoothbuild.task.exec;

import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.Result;

public class TaskContainer implements Result {
  private final TaskExecutor taskExecutor;
  private final LocatedTask task;

  public TaskContainer(TaskExecutor taskExecutor, LocatedTask task) {
    this.taskExecutor = taskExecutor;
    this.task = task;
  }

  @Override
  public Value result() {
    return taskExecutor.execute(task);
  }
}
