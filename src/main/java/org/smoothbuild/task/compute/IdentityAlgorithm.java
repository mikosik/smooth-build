package org.smoothbuild.task.compute;

import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

public class IdentityAlgorithm implements Algorithm {
  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return new TaskOutput(input.values().get(0));
  }
}
