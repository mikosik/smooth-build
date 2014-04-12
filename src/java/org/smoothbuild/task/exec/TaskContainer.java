package org.smoothbuild.task.exec;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;

public class TaskContainer<T extends SValue> implements Result<T> {
  private final TaskExecutor taskExecutor;
  private final Task<T> task;
  private T result;

  public TaskContainer(TaskExecutor taskExecutor, Task<T> task) {
    this.taskExecutor = taskExecutor;
    this.task = task;
    this.result = null;
  }

  @Override
  public T value() {
    if (result == null) {
      result = taskExecutor.execute(task);
    }
    return result;
  }
}
