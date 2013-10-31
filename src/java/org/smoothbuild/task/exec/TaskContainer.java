package org.smoothbuild.task.exec;

import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.base.Result;

public class TaskContainer implements Result {
  private final TaskExecutor taskExecutor;
  private final LocatedTask task;
  private Value result;

  public TaskContainer(TaskExecutor taskExecutor, LocatedTask task) {
    this.taskExecutor = taskExecutor;
    this.task = task;
    this.result = null;
  }

  @Override
  public Value result() {
    if (result == null) {
      result = taskExecutor.execute(task);
    }
    return result;
  }
}
