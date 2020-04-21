package org.smoothbuild.exec.task.base;

public class TaskAttributes {
  private final TaskType type;
  private final TaskSpace space;

  public TaskAttributes(TaskType type, TaskSpace space) {
    this.type = type;
    this.space = space;
  }

  public TaskType type() {
    return type;
  }

  public TaskSpace space() {
    return space;
  }
}
