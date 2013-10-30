package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class InvalidNode extends AbstractDefinitionNode {
  private final Type type;

  public InvalidNode(Type type, CodeLocation codeLocation) {
    super(CallLocation.callLocation("invalid", codeLocation));
    this.type = checkNotNull(type);
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    throw new RuntimeException("InvalidNode.generateTask() should not be called.");
  }
}
