package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.EMPTY_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public class EmptySetNode implements DefinitionNode {

  public EmptySetNode() {}

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
