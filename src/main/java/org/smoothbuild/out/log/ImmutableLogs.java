package org.smoothbuild.out.log;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

public class ImmutableLogs implements Logs {
  private final ImmutableList<Log> logs;

  public static ImmutableLogs logs(Log... logs) {
    return new ImmutableLogs(ImmutableList.copyOf(logs));
  }

  public static ImmutableLogs logs(Collection<? extends Log> logs) {
    return new ImmutableLogs(ImmutableList.copyOf(logs));
  }

  private ImmutableLogs(List<Log> logs) {
    this.logs = ImmutableList.copyOf(logs);
  }

  @Override
  public Stream<Log> stream() {
    return logs.stream();
  }

  @Override
  public ImmutableList<Log> toList() {
    return logs;
  }

  @Override
  public ImmutableLogs toImmutableLogs() {
    return this;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ImmutableLogs that
        && logs.equals(that.logs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(logs);
  }

  @Override
  public String toString() {
    return toCommaSeparatedString(logs, Log::toString);
  }
}
