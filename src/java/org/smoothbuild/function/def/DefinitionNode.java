package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.TaskGenerator;
import org.smoothbuild.task.base.Task;

public interface DefinitionNode {
  public Type type();

  public Task generateTask(TaskGenerator taskGenerator);
}
