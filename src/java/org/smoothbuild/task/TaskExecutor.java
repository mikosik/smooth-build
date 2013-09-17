package org.smoothbuild.task;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.plugin.api.Path.path;

import java.util.Collection;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.plugin.internal.SandboxImpl;
import org.smoothbuild.problem.DetectingErrorsMessageListener;
import org.smoothbuild.problem.MessageListener;

public class TaskExecutor {
  private final FileSystem fileSystem;

  @Inject
  public TaskExecutor(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void execute(MessageListener messageListener, Task task) {
    new Worker(messageListener).execute(task);
  }

  private class Worker {
    private final DetectingErrorsMessageListener messages;
    private int temptDirCount = 0;

    public Worker(MessageListener messageListener) {
      this.messages = new DetectingErrorsMessageListener(messageListener);
    }

    private void execute(Task task) {
      calculateTasks(task.dependencies());

      if (messages.errorDetected()) {
        return;
      }

      Path tempPath = BUILD_DIR.append(path(Integer.toString(temptDirCount++)));
      task.execute(new SandboxImpl(fileSystem, tempPath, messages));
    }

    private void calculateTasks(Collection<Task> tasks) {
      for (Task task : tasks) {
        if (!task.isResultCalculated()) {
          execute(task);
        }
        if (messages.errorDetected()) {
          return;
        }
      }
    }
  }
}
