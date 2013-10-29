package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Task;

public class InvalidNode extends AbstractDefinitionNode {
  private final Type type;

  public InvalidNode(Type type, CodeLocation codeLocation) {
    super(codeLocation);
    this.type = checkNotNull(type);
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public Task generateTask() {
    throw new RuntimeException("InvalidNode.generateTask() should not be called.");
  }
}
