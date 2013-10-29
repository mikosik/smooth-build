package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.EMPTY_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.EmptySetTask;
import org.smoothbuild.task.base.Task;

public class EmptySetNode implements DefinitionNode {
  private final CodeLocation codeLocation;

  public EmptySetNode(CodeLocation codeLocation) {
    this.codeLocation = codeLocation;
  }

  @Override
  public Type type() {
    return EMPTY_SET;
  }

  @Override
  public Task generateTask() {
    return new EmptySetTask(codeLocation);
  }
}
