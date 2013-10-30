package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.db.ValueDb;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.LocatedTask;

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

  public Value execute(LocatedTask task) {
    SandboxImpl sandbox = createSandbox(task);
    Value result = task.execute(sandbox);
    userConsole.report(sandbox.messageGroup());
    return result;
  }

  private SandboxImpl createSandbox(LocatedTask task) {
    return new SandboxImpl(fileSystem, valueDb, task.name(), task.codeLocation());
  }
}
