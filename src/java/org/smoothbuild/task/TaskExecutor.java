package org.smoothbuild.task;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.fs.base.Path.path;

import java.util.Collection;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;

import com.google.common.hash.HashCode;

public class TaskExecutor {
  private final FileSystem fileSystem;

  @Inject
  public TaskExecutor(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void execute(MessageListener messageListener, HashedTasks tasks, HashCode hash) {
    new Worker(messageListener, tasks).execute(hash);
  }

  private class Worker {
    private final DetectingErrorsMessageListener messages;
    private final HashedTasks hashedTasks;
    private int temptDirCount = 0;

    public Worker(MessageListener messageListener, HashedTasks tasks) {
      this.messages = new DetectingErrorsMessageListener(messageListener);
      this.hashedTasks = tasks;
    }

    private void execute(HashCode hash) {
      Task task = hashedTasks.get(hash);
      if (task.isResultCalculated()) {
        return;
      }

      calculateTasks(task.dependencies());

      if (messages.errorDetected()) {
        return;
      }

      Path tempPath = BUILD_DIR.append(path(Integer.toString(temptDirCount++)));
      SandboxImpl sandbox = new SandboxImpl(fileSystem, tempPath, task.location());
      task.execute(sandbox, hashedTasks);
      sandbox.reportCollectedMessagesTo(messages);
    }

    private void calculateTasks(Collection<HashCode> tasks) {
      for (HashCode taskHash : tasks) {
        execute(taskHash);
        if (messages.errorDetected()) {
          return;
        }
      }
    }
  }
}
