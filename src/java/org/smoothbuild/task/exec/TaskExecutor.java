package org.smoothbuild.task.exec;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.fs.base.Path.path;

import java.util.Collection;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.listen.DetectingErrorsMessageListener;
import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;

public class TaskExecutor {
  private final FileSystem fileSystem;
  private final HashedTasks hashedTasks;

  @Inject
  public TaskExecutor(FileSystem fileSystem, HashedTasks hashedTasks) {
    this.fileSystem = fileSystem;
    this.hashedTasks = hashedTasks;
  }

  public void execute(MessageListener messageListener, HashCode hash) {
    new Worker(messageListener, hashedTasks).execute(hash);
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
