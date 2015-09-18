package org.smoothbuild.task.compute;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

public class IdentityAlgorithm implements Algorithm {
  private final Type type;

  public IdentityAlgorithm(Type type) {
    this.type = type;
  }

  @Override
  public Type resultType() {
    return type;
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return new TaskOutput(input.values().get(0));
  }
}
