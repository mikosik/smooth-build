package org.smoothbuild.cli.console;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.ArrayList;
import java.util.List;

public class LoggerImpl implements Logger, AutoCloseable {
  private final String header;
  private final Console console;
  private final List<Log> logs;
  private boolean hasProblems;

  public LoggerImpl(String header, Console console) {
    this.header = header;
    this.console = console;
    this.logs = new ArrayList<>();
    this.hasProblems = false;
  }

  @Override
  public void fatal(String message) {
    log(new Log(FATAL, message));
  }

  @Override
  public void error(String message) {
    log(new Log(ERROR, message));
  }

  @Override
  public void warning(String message) {
    log(new Log(WARNING, message));
  }

  @Override
  public void info(String message) {
    log(new Log(INFO, message));
  }

  @Override
  public void log(Log log) {
    if (log.level() == FATAL || log.level() == ERROR) {
      hasProblems = true;
    }
    logs.add(log);
  }

  public boolean hasProblems() {
    return hasProblems;
  }

  @Override
  public void close() {
    console.show(header, logs);
  }
}
