package org.smoothbuild.out.log;

public enum Level {
  FATAL,
  ERROR,
  WARNING,
  INFO;

  public boolean hasPriorityAtLeast(Level priority) {
    return this.ordinal() <= priority.ordinal();
  }
}
