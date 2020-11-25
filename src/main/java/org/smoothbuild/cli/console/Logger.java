package org.smoothbuild.cli.console;

public interface Logger {
  public void log(Log log);

  public void fatal(String message);

  public void error(String message);

  public void warning(String message);

  public void info(String message);

  public default void logAll(Iterable<? extends Log> logs) {
    logs.forEach(this::log);
  }
}
