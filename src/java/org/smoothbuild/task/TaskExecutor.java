package org.smoothbuild.task;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.fs.base.Path.path;

import java.util.Collection;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;

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
      if (task.isResultCalculated()) {
        return;
      }

      calculateTasks(task.dependencies());

      if (messages.errorDetected()) {
        return;
      }

      Path tempPath = BUILD_DIR.append(path(Integer.toString(temptDirCount++)));
      SandboxImpl sandbox = new SandboxImpl(fileSystem, tempPath, task.location());
      task.execute(sandbox);
      sandbox.reportCollectedMessagesTo(messages);
    }

    private void calculateTasks(Collection<Task> tasks) {
      for (Task task : tasks) {
        execute(task);
        if (messages.errorDetected()) {
          return;
        }
      }
    }
  }
}
