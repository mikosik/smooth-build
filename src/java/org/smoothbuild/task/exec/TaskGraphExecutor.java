package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

public class TaskGraphExecutor {
  private final UserConsole userConsole;
  private final TaskExecutor taskExecutor;

  @Inject
  public TaskGraphExecutor(UserConsole userConsole, TaskExecutor taskExecutor) {
    this.userConsole = userConsole;
    this.taskExecutor = taskExecutor;
  }

  public void execute(Task task) {
    for (Task dependency : task.dependencies()) {
      execute(dependency);
      if (userConsole.isErrorReported()) {
        return;
      }
    }

    taskExecutor.execute(task);
  }
}
