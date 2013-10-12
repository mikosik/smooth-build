package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.TaskGenerator;

public interface DefinitionNode {
  public Type type();

  public Task generateTask(TaskGenerator taskGenerator);
}
