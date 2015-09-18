package org.smoothbuild.task.compute;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

public interface Algorithm {
  public Type resultType();

  public TaskOutput execute(TaskInput input, ContainerImpl container);
}
