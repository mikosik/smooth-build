package org.smoothbuild.common.log.base;

public enum Level {
  FATAL,
  ERROR,
  WARNING,
  INFO;

  public boolean hasPriorityAtLeast(Level priority) {
    return this.ordinal() <= priority.ordinal();
  }
}
