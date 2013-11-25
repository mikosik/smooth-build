package org.smoothbuild.task.exec;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;

public class TaskContainer implements Result {
  private final TaskExecutor taskExecutor;
  private final Task task;
  private SValue result;

  public TaskContainer(TaskExecutor taskExecutor, Task task) {
    this.taskExecutor = taskExecutor;
    this.task = task;
    this.result = null;
  }

  @Override
  public SValue result() {
    if (result == null) {
      result = taskExecutor.execute(task);
    }
    return result;
  }
}
