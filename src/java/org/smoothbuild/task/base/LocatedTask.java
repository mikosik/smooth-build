package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;

public class LocatedTask implements Task {
  private final Task task;
  private final CodeLocation codeLocation;

  public LocatedTask(Task task, CodeLocation codeLocation) {
    this.task = checkNotNull(task);
    this.codeLocation = checkNotNull(codeLocation);
  }

  @Override
  public String name() {
    return task.name();
  }

  @Override
  public boolean isInternal() {
    return task.isInternal();
  }

  @Override
  public Value execute(Sandbox sandbox) {
    return task.execute(sandbox);
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }
}
