package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.LocatedTask;

public class TaskExecutor {
  private final SandboxFactory sandboxFactory;
  private final UserConsole userConsole;

  @Inject
  public TaskExecutor(SandboxFactory sandboxFactory, UserConsole userConsole) {
    this.sandboxFactory = sandboxFactory;
    this.userConsole = userConsole;
  }

  public Value execute(LocatedTask task) {
    SandboxImpl sandbox = sandboxFactory.createSandbox(task);
    Value result = task.execute(sandbox);

    MessageGroup messageGroup = sandbox.messageGroup();
    if (!task.isInternal() || messageGroup.containsMessages()) {
      userConsole.report(messageGroup);
    }
    return result;
  }
}
