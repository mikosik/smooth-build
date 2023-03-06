package org.smoothbuild.out.log;

public enum Level {
  FATAL(true),
  ERROR(true),
  WARNING(false),
  INFO(false);

  private final boolean isAProblem;

  Level(boolean isAProblem) {
    this.isAProblem = isAProblem;
  }

  public boolean isAProblem() {
    return isAProblem;
  }

  public boolean hasPriorityAtLeast(Level priority) {
    return this.ordinal() <= priority.ordinal();
  }
}
