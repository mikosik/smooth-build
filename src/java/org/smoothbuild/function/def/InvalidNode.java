package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.message.message.MessageType.FATAL;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class InvalidNode extends AbstractNode {
  private final Type type;

  public InvalidNode(Type type, CodeLocation codeLocation) {
    super(type, codeLocation);
    this.type = checkNotNull(type);
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    throw new ErrorMessageException(new Message(FATAL,
        "Bug in smooth binary: InvalidNode.generateTask() should not be called."));
  }
}
