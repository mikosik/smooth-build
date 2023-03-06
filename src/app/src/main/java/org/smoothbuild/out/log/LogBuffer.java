package org.smoothbuild.out.log;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.util.collect.Iterables.joinWithCommaToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogBuffer extends ArrayList<Log> implements Logger, Logs {
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
    add(log);
  }

  @Override
  public List<Log> toList() {
    return this;
  }

  @Override
  public ImmutableLogs toImmutableLogs() {
    return ImmutableLogs.logs(this);
  }

  @Override
  public Stream<Log> stream() {
    return super.stream();
  }

  @Override
  public String toString() {
    return joinWithCommaToString(this, Log::toString);
  }
}
