package org.smoothbuild.exec.task.base;

import org.smoothbuild.lang.base.Space;

public class SimpleTask implements Task {
  private final TaskKind kind;
  private final Space space;

  public SimpleTask(TaskKind kind, Space space) {
    this.kind = kind;
    this.space = space;
  }

  @Override
  public TaskKind kind() {
    return kind;
  }

  @Override
  public Space space() {
    return space;
  }
}
