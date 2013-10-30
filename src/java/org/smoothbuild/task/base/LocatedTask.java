package org.smoothbuild.task.base;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.Value;

public class LocatedTask implements Task {
  private final Task task;
  private final CodeLocation codeLocation;

  public LocatedTask(Task task, CodeLocation codeLocation) {
    this.task = task;
    this.codeLocation = codeLocation;
  }

  @Override
  public String name() {
    return task.name();
  }

  @Override
  public Value execute(Sandbox sandbox) {
    return task.execute(sandbox);
  }

  public CodeLocation codeLocation() {
    return codeLocation;
  }
}
