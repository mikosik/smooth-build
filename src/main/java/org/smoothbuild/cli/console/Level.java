package org.smoothbuild.cli.console;

public enum Level {
  FATAL,
  ERROR,
  WARNING,
  INFO;

  public boolean hasPriorityAtLeast(Level priority) {
    return this.ordinal() <= priority.ordinal();
  }
}
