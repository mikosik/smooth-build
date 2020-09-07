package org.smoothbuild.cli.console;

import static java.util.Collections.unmodifiableList;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.ArrayList;
import java.util.List;

public class ValueWithLogs<V> implements Logger {
  private V value;
  private final List<Log> logs;
  private boolean hasProblems;

  public ValueWithLogs() {
    this.value = null;
    this.logs = new ArrayList<>();
    this.hasProblems = false;
  }

  public <T> ValueWithLogs(ValueWithLogs<T> valueWithLogs) {
    this.value = null;
    this.logs = new ArrayList<>(valueWithLogs.logs);
    this.hasProblems = valueWithLogs.hasProblems;
  }

  public void setValue(V value) {
    this.value = value;
  }

  public V value() {
    return value;
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

  public <T> void addLogs(ValueWithLogs<T> valueWithLogs) {
    logs.addAll(valueWithLogs.logs);
    hasProblems = hasProblems || valueWithLogs.hasProblems;
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
}
