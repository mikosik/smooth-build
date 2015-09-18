package org.smoothbuild.task.compute;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

public class ConstantAlgorithm implements Algorithm {
  private final Value value;

  public ConstantAlgorithm(Value value) {
    this.value = value;
  }

  @Override
  public Type resultType() {
    return value.type();
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return new TaskOutput(value);
  }
}
