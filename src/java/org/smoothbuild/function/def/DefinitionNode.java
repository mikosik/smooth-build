package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;

public interface DefinitionNode {
  public Type type();

  public Task generateTask();
}
