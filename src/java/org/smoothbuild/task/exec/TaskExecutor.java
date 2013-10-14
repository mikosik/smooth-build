package org.smoothbuild.task.exec;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.fs.base.Path.path;

import java.util.Collection;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;

public class TaskExecutor {
  private final FileSystem fileSystem;
  private final HashedTasks hashedTasks;
  private final UserConsole userConsole;

  @Inject
  public TaskExecutor(FileSystem fileSystem, HashedTasks hashedTasks, UserConsole userConsole) {
    this.fileSystem = fileSystem;
    this.hashedTasks = hashedTasks;
    this.userConsole = userConsole;
  }

  public void execute(HashCode hash) {
    new Worker(userConsole, hashedTasks).execute(hash);
  }

  private class Worker {
    private final UserConsole userConsole;
    private final HashedTasks hashedTasks;
    private int temptDirCount = 0;

    public Worker(UserConsole userConsole, HashedTasks tasks) {
      this.userConsole = userConsole;
      this.hashedTasks = tasks;
    }

    private void execute(HashCode hash) {
      Task task = hashedTasks.get(hash);
      if (task.isResultCalculated()) {
        return;
      }

      calculateTasks(task.dependencies());

      if (userConsole.isErrorReported()) {
        return;
      }

      Path tempPath = BUILD_DIR.append(path(Integer.toString(temptDirCount++)));
      SandboxImpl sandbox = new SandboxImpl(fileSystem, tempPath, task.location());
      task.execute(sandbox, hashedTasks);
      userConsole.report(sandbox.messageGroup());
    }

    private void calculateTasks(Collection<HashCode> tasks) {
      for (HashCode taskHash : tasks) {
        execute(taskHash);
        if (userConsole.isErrorReported()) {
          return;
        }
      }
    }
  }
}
