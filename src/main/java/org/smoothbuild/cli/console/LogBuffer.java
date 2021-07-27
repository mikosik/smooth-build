package org.smoothbuild.cli.console;

import static java.util.Collections.unmodifiableList;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.ArrayList;
import java.util.List;

public class LogBuffer implements Logger, Logs {
  private final List<Log> logs;
  private boolean containsProblem;

  public LogBuffer() {
    this.logs = new ArrayList<>();
    this.containsProblem = false;
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
  public void logAll(Logs logs) {
    this.logs.addAll(logs.toList());
    this.containsProblem = this.containsProblem || logs.containsProblem();
  }

  @Override
  public void log(Log log) {
    containsProblem = containsProblem || log.level().isAProblem();
    logs.add(log);
  }

  @Override
  public boolean containsProblem() {
    return containsProblem;
  }

  @Override
  public List<Log> toList() {
    return unmodifiableList(logs);
  }

  @Override
  public ImmutableLogs toImmutableLogs() {
    return new ImmutableLogs(logs);
  }
}