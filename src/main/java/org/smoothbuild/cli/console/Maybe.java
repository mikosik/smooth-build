package org.smoothbuild.cli.console;

import static java.util.Collections.unmodifiableList;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.ArrayList;
import java.util.List;

public class Maybe<V> implements Logger {
  private V value;
  private final List<Log> logs;
  private boolean hasProblems;

  public Maybe() {
    this.value = null;
    this.logs = new ArrayList<>();
    this.hasProblems = false;
  }

  public <T> Maybe(Maybe<T> maybe) {
    this.value = null;
    this.logs = new ArrayList<>(maybe.logs);
    this.hasProblems = maybe.hasProblems;
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

  public <T> void addLogs(Maybe<T> maybe) {
    logs.addAll(maybe.logs);
    hasProblems = hasProblems || maybe.hasProblems;
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
