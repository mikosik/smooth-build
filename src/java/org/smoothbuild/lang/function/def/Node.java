package org.smoothbuild.lang.function.def;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.Taskable;
import org.smoothbuild.task.exec.TaskGenerator;

public abstract class Node<T extends SValue> implements Taskable<T> {
  private final SType<T> type;
  private final CodeLocation codeLocation;

  public Node(SType<T> type, CodeLocation codeLocation) {
    this.type = checkNotNull(type);
    this.codeLocation = checkNotNull(codeLocation);
  }

  public SType<T> type() {
    return type;
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }

  @Override
  public abstract Task<T> generateTask(TaskGenerator taskGenerator);
}
