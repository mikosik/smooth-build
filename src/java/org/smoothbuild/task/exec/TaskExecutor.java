package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.lang.plugin.Value;
import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.task.base.Task;

public class TaskExecutor {
  private final SandboxFactory sandboxFactory;
  private final UserConsole userConsole;

  @Inject
  public TaskExecutor(SandboxFactory sandboxFactory, UserConsole userConsole) {
    this.sandboxFactory = sandboxFactory;
    this.userConsole = userConsole;
  }

  public Value execute(Task task) {
    SandboxImpl sandbox = sandboxFactory.createSandbox(task);
    Value result = task.execute(sandbox);

    MessageGroup messageGroup = sandbox.messageGroup();
    if (!task.isInternal() || messageGroup.containsMessages()) {
      userConsole.report(messageGroup);
    }
    if (messageGroup.containsProblems()) {
      throw new BuildInterruptedException();
    }
    return result;
  }
}
