package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.message.base.MessageType.FATAL;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class InvalidNode<T extends SValue> extends Node<T> {
  private final SType<T> type;

  public InvalidNode(SType<T> type, CodeLocation codeLocation) {
    super(type, codeLocation);
    this.type = checkNotNull(type);
  }

  @Override
  public SType<T> type() {
    return type;
  }

  @Override
  public Task<T> generateTask(TaskGenerator taskGenerator) {
    throw new Message(FATAL,
        "Bug in smooth binary: InvalidNode.generateTask() should not be called.");
  }
}
