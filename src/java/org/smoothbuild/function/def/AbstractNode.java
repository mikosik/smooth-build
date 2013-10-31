package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.exec.TaskGenerator;

public abstract class AbstractNode implements Node {
  private final CodeLocation codeLocation;

  public AbstractNode(CodeLocation codeLocation) {
    this.codeLocation = checkNotNull(codeLocation);
  }

  @Override
  public CodeLocation codeLocation() {
    return codeLocation;
  }

  @Override
  public abstract Type type();

  @Override
  public abstract LocatedTask generateTask(TaskGenerator taskGenerator);
}
