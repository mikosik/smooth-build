package org.smoothbuild.common.log;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

public record Log(Level level, String message) {
  public Log(Level level, String message) {
    this.level = requireNonNull(level);
    this.message = requireNonNull(message);
  }

  public static Log fatal(String log) {
    return new Log(Level.FATAL, log);
  }

  public static Log error(String log) {
    return new Log(Level.ERROR, log);
  }

  public static Log warning(String log) {
    return new Log(Level.WARNING, log);
  }

  public static Log info(String log) {
    return new Log(Level.INFO, log);
  }

  public static boolean containsAnyFailure(Collection<Log> list) {
    return list.stream().anyMatch(l -> l.level().hasPriorityAtLeast(Level.ERROR));
  }

  public String toPrettyString() {
    return "[" + level + "] " + message;
  }

  @Override
  public String toString() {
    return "Log{" + level + ", '" + message + "'}";
  }
}
