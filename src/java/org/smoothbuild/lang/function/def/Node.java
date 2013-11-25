package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;
import org.smoothbuild.task.exec.TaskGenerator;

public abstract class Node implements Taskable {
  private final SType<?> type;
  private final CodeLocation codeLocation;

  public Node(SType<?> type, CodeLocation codeLocation) {
    this.type = checkNotNull(type);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public SType<?> type() {
    return type;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  @Override
  public abstract Task generateTask(TaskGenerator taskGenerator);
}
