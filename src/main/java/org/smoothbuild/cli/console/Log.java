package org.smoothbuild.cli.console;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.List;
import java.util.Objects;

/**
 * This class is immutable.
 */
public final class Log {
  private final Level level;
  private final String message;

  public Log(Level level, String message) {
    this.level = requireNonNull(level);
    this.message = requireNonNull(message);
  }

  public static Log fatal(String log) {
    return new Log(FATAL, log);
  }

  public static Log error(String log) {
    return new Log(ERROR, log);
  }

  public static Log warning(String log) {
    return new Log(WARNING, log);
  }

  public static Log info(String log) {
    return new Log(INFO, log);
  }

  public Level level() {
    return level;
  }

  public String message() {
    return message;
  }

  public static List<Log> asLogs(Level level, List<String> messages) {
    return messages.stream()
        .map(m -> new Log(level, m))
        .collect(toList());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Log log = (Log) o;
    return level == log.level && message.equals(log.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(level, message);
  }

  @Override
  public String toString() {
    return "Log{" + level + ", '" + message + "'}";
  }
}
