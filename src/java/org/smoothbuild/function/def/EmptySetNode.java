package org.smoothbuild.function.def;

import static org.smoothbuild.function.base.Type.EMPTY_SET;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.Task;

public class EmptySetNode implements DefinitionNode {
  public static EmptySetNode INSTANCE = new EmptySetNode();

  public static EmptySetNode emptySetNode() {
    return INSTANCE;
  }

  private EmptySetNode() {}

  @Override
  public Type type() {
    return EMPTY_SET;
  }

  @Override
  public Task generateTask() {
    throw new UnsupportedOperationException("Cannot call EmptySetNode.generateTask(). "
        + "EmptySetNode should have been replaced by either StringSetNode or FileSetNode "
        + "during argument->parameters assignment.");
  }

}
