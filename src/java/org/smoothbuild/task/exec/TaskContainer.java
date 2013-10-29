package org.smoothbuild.task.exec;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;

public class TaskContainer implements Result {
  private final TaskExecutor taskExecutor;
  private final Task task;
  private final CallLocation callLocation;

  public TaskContainer(TaskExecutor taskExecutor, Task task, CallLocation callLocation) {
    this.taskExecutor = taskExecutor;
    this.task = task;
    this.callLocation = callLocation;
  }

  @Override
  public Value result() {
    return taskExecutor.execute(task, callLocation);
  }
}
