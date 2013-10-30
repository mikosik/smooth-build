package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.db.ValueDb;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.Value;
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

  public Value execute(Task task, CallLocation callLocation) {
    SandboxImpl sandbox = createSandbox(callLocation);
    Value result = task.execute(sandbox);
    userConsole.report(sandbox.messageGroup());
    return result;
  }

  private SandboxImpl createSandbox(CallLocation callLocation) {
    return new SandboxImpl(fileSystem, valueDb, callLocation);
  }
}
