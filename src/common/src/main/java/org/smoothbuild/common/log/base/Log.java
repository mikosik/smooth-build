package org.smoothbuild.common.log.base;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.collect.Streams.stream;
import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;

public record Log(Level level, String message) {
  public Log(Level level, String message) {
    this.level = requireNonNull(level);
    this.message = requireNonNull(message);
  }

  public static Log fatal(String message, Throwable throwable) {
    return fatal(message + "\n" + getStackTraceAsString(throwable).stripTrailing());
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

  public static boolean containsFailure(Iterable<? extends Log> iterable) {
    return stream(iterable).anyMatch(l -> l.level().hasPriorityAtLeast(ERROR));
  }

  public String toPrettyString() {
    return "[" + level + "] " + message;
  }

  @Override
  public String toString() {
    return "Log{" + level + ", '" + message + "'}";
  }
}
