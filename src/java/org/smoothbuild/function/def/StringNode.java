package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.task.StringTask;
import org.smoothbuild.task.Task;
import org.smoothbuild.task.TaskGenerator;

public class StringNode implements DefinitionNode {
  private final String string;

  public StringNode(String string) {
    this.string = checkNotNull(string);
  }

  @Override
  public Type type() {
    return Type.STRING;
  }

  @Override
  public Task generateTask(TaskGenerator taskGenerator) {
    return new StringTask(string);
  }
}
