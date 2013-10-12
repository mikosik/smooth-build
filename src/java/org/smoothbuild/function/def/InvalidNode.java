package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.TaskGenerator;

public class InvalidNode implements DefinitionNode {
  private final Type type;

  InvalidNode(Type type) {
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
