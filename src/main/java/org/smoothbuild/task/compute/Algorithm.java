package org.smoothbuild.task.compute;

import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

public interface Algorithm {
  public TaskOutput execute(TaskInput input, ContainerImpl container);
}
