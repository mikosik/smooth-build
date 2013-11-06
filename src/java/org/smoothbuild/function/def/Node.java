package org.smoothbuild.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;
import org.smoothbuild.task.exec.TaskGenerator;

public abstract class Node implements Taskable {
  private final Type type;
  private final CodeLocation codeLocation;

  public Node(Type type, CodeLocation codeLocation) {
    this.type = checkNotNull(type);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public Type type() {
    return type;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  @Override
  public abstract Task generateTask(TaskGenerator taskGenerator);
}
