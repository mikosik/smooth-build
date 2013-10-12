package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.EMPTY_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.TaskGenerator;

public class EmptySetNode implements DefinitionNode {

  EmptySetNode() {}

  @Override
  public Type type() {
    return EMPTY_SET;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    throw new UnsupportedOperationException("Cannot call EmptySetNode.generateTask(). "
        + "EmptySetNode should have been replaced by either StringSetNode or FileSetNode "
        + "during argument->parameters assignment.");
  }

}
