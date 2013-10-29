package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Task;

public abstract class AbstractDefinitionNode implements DefinitionNode {
  private final CodeLocation codeLocation;

  public AbstractDefinitionNode(CodeLocation codeLocation) {
    this.codeLocation = checkNotNull(codeLocation);
  }

  @Override
  public CodeLocation codeLocation() {
    return codeLocation;
  }

  @Override
  public abstract Type type();

  @Override
  public abstract Task generateTask();
}
