package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;

public abstract class AbstractNode implements Node {
  private final Type type;
  private final CodeLocation codeLocation;

  public AbstractNode(Type type, CodeLocation codeLocation) {
    this.type = checkNotNull(type);
    this.codeLocation = checkNotNull(codeLocation);
  }

  @Override
  public Type type() {
    return type;
  }

  @Override
  public CodeLocation codeLocation() {
    return codeLocation;
  }

  @Override
  public abstract Task generateTask(TaskGenerator taskGenerator);
}
