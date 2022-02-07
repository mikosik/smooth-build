package org.smoothbuild.out.log;

public interface Logger {
  public default void logAll(Iterable<? extends Log> logs) {
    logs.forEach(this::log);
  }

  public void logAll(Logs logs);

  public void log(Log log);

  public void fatal(String message);

  public void error(String message);

  public void warning(String message);

  public void info(String message);
}
