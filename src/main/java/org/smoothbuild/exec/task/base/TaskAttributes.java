package org.smoothbuild.exec.task.base;

public class TaskAttributes {
  private final TaskKind type;
  private final TaskSpace space;

  public TaskAttributes(TaskKind type, TaskSpace space) {
    this.type = type;
    this.space = space;
  }

  public TaskKind type() {
    return type;
  }

  public TaskSpace space() {
    return space;
  }
}
