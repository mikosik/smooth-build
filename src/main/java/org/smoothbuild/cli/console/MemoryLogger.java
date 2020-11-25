package org.smoothbuild.cli.console;

import static java.util.Collections.unmodifiableList;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.ArrayList;
import java.util.List;

public class MemoryLogger implements Logger {
  private final List<Log> logs;
  private boolean hasProblems;

  public MemoryLogger() {
    this.logs = new ArrayList<>();
    this.hasProblems = false;
  }

  public MemoryLogger(MemoryLogger logger) {
    this.logs = new ArrayList<>(logger.logs);
    this.hasProblems = logger.hasProblems;
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

  public List<Log> logs() {
    return unmodifiableList(logs);
  }

  public void logAllFrom(MemoryLogger logger) {
    logs.addAll(logger.logs);
    hasProblems = hasProblems || logger.hasProblems;
  }
}
