package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.object.ValueDb;
import org.smoothbuild.task.base.Task;

public class TaskExecutor {
  private final FileSystem fileSystem;
  private final ValueDb valueDb;
  private final UserConsole userConsole;

  @Inject
  public TaskExecutor(FileSystem fileSystem, ValueDb valueDb, UserConsole userConsole) {
    this.fileSystem = fileSystem;
    this.valueDb = valueDb;
    this.userConsole = userConsole;
  }

  public void execute(Task task) {
    if (task.isResultCalculated()) {
      return;
    }

    SandboxImpl sandbox = createSandbox(task);
    task.execute(sandbox);
    userConsole.report(sandbox.messageGroup());
  }

  private SandboxImpl createSandbox(Task task) {
    return new SandboxImpl(fileSystem, valueDb, task.location());
  }
}
