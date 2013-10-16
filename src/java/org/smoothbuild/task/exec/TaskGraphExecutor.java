package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;

public class TaskGraphExecutor {
  private final HashedTasks hashedTasks;
  private final UserConsole userConsole;
  private final TaskExecutor taskExecutor;

  @Inject
  public TaskGraphExecutor(HashedTasks hashedTasks, UserConsole userConsole,
      TaskExecutor taskExecutor) {
    this.hashedTasks = hashedTasks;
    this.userConsole = userConsole;
    this.taskExecutor = taskExecutor;
  }

  public void execute(HashCode hash) {
    Task task = hashedTasks.get(hash);

    for (HashCode dependencyHash : task.dependencies()) {
      execute(dependencyHash);
      if (userConsole.isErrorReported()) {
        return;
      }
    }

    taskExecutor.execute(hash);
  }
}
