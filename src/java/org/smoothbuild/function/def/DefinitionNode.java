package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public interface DefinitionNode {

  public CallLocation callLocation();

  public Type type();

  public Task generateTask(TaskGenerator taskGenerator);
}
