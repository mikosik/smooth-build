package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.object.ObjectsDb;
import org.smoothbuild.task.base.Task;

import com.google.common.hash.HashCode;

public class TaskExecutor {
  private final FileSystem fileSystem;
  private final ObjectsDb objectsDb;
  private final HashedTasks hashedTasks;
  private final UserConsole userConsole;

  @Inject
  public TaskExecutor(FileSystem fileSystem, ObjectsDb objectsDb, HashedTasks hashedTasks,
      UserConsole userConsole) {
    this.fileSystem = fileSystem;
    this.objectsDb = objectsDb;
    this.hashedTasks = hashedTasks;
    this.userConsole = userConsole;
  }

  public void execute(HashCode hash) {
    Task task = hashedTasks.get(hash);
    if (task.isResultCalculated()) {
      return;
    }

    SandboxImpl sandbox = createSandbox(task);
    task.execute(sandbox, hashedTasks);
    userConsole.report(sandbox.messageGroup());
  }

  private SandboxImpl createSandbox(Task task) {
    return new SandboxImpl(fileSystem, objectsDb, task.location());
  }
}
