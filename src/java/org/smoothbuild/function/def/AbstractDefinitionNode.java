package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public abstract class AbstractDefinitionNode implements DefinitionNode {
  private final CallLocation callLocation;

  public AbstractDefinitionNode(CallLocation callLocation) {
    this.callLocation = checkNotNull(callLocation);
  }

  @Override
  public CallLocation callLocation() {
    return callLocation;
  }

  @Override
  public abstract Type type();

  @Override
  public abstract Task generateTask(TaskGenerator taskGenerator);
}
