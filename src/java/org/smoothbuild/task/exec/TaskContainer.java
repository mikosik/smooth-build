package org.smoothbuild.task.exec;

import org.smoothbuild.lang.plugin.Value;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;

public class TaskContainer implements Result {
  private final TaskExecutor taskExecutor;
  private final Task task;
  private Value result;

  public TaskContainer(TaskExecutor taskExecutor, Task task) {
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
