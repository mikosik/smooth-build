package org.smoothbuild.out.log;

import static org.smoothbuild.common.collect.List.list;

import java.util.Iterator;
import java.util.Objects;
import org.smoothbuild.common.collect.List;

public class ImmutableLogs implements Logs {
  private final List<Log> logs;

  public static ImmutableLogs logs(Log... logs) {
    return new ImmutableLogs(list(logs));
  }

  public static ImmutableLogs logs(List<Log> logs) {
    return new ImmutableLogs(logs);
  }

  private ImmutableLogs(List<Log> logs) {
    this.logs = logs;
  }

  @Override
  public Iterator<Log> iterator() {
    return logs.iterator();
  }

  @Override
  public List<Log> toList() {
    return logs;
  }

  @Override
  public ImmutableLogs toImmutableLogs() {
    return this;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof ImmutableLogs that && logs.equals(that.logs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(logs);
  }

  @Override
  public String toString() {
    return logs.toString(",");
  }
}
