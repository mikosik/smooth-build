package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.message.listen.MessageCatchingExecutor;
import org.smoothbuild.message.listen.UserConsole;

public class SmoothExecutorPhase extends MessageCatchingExecutor<ExecutionData, Void> {
  private final SmoothExecutor smoothExecutor;

  @Inject
  public SmoothExecutorPhase(UserConsole userConsole, SmoothExecutor smoothExecutor,
      SmoothExecutorMessages messages) {
    super(userConsole, "SMOOTH EXECUTOR", messages);
    this.smoothExecutor = smoothExecutor;
  }

  @Override
  public Void executeImpl(ExecutionData executionData) {
    smoothExecutor.execute(executionData);
    return null;
  }
}
