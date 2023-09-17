package org.smoothbuild.out.log;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;

public interface Logger {
  public default void logAll(Iterable<? extends Log> logs) {
    logs.forEach(this::log);
  }

  public default void fatal(String message) {
    log(new Log(FATAL, message));
  }

  public default void error(String message) {
    log(new Log(ERROR, message));
  }

  public default void warning(String message) {
    log(new Log(WARNING, message));
  }

  public default void info(String message) {
    log(new Log(INFO, message));
  }

  public void log(Log log);
}
