package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;

public class InvalidNode implements DefinitionNode {
  private final Type type;

  public InvalidNode(Type type) {
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
