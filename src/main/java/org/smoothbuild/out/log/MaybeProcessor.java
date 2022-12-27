package org.smoothbuild.out.log;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;

public abstract class MaybeProcessor<T> {
  private final LogBuffer logBuffer = new LogBuffer();

  public Maybe<T> process() {
    try {
      return maybe(processImpl(), logBuffer);
    } catch (FailedException e) {
      return maybeLogs(logBuffer);
    }
  }

  protected abstract T processImpl() throws FailedException;

  protected <V> V addLogsAndGetValue(Maybe<V> maybe) throws FailedException {
    addLogs(maybe.logs());
    return maybe.value();
  }

  protected void addLogs(Logs logs) throws FailedException {
    logBuffer.logAll(logs);
    if (logBuffer.containsAtLeast(ERROR)) {
      throw new FailedException();
    }
  }

  protected static class FailedException extends Exception {}
}
