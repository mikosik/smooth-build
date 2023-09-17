package org.smoothbuild.out.log;

import static org.smoothbuild.common.collect.Iterables.joinWithCommaToString;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LogBuffer extends ArrayList<Log> implements Logger, Logs {
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
