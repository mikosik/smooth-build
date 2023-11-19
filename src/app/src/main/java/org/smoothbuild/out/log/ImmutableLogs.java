package org.smoothbuild.out.log;

import static org.smoothbuild.common.collect.Iterables.joinWithCommaToString;

import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
  public Iterator<Log> iterator() {
    return logs.iterator();
  }

  @Override
  public void forEach(Consumer<? super Log> action) {
    logs.forEach(action);
  }

  @Override
  public Spliterator<Log> spliterator() {
    return logs.spliterator();
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
    return joinWithCommaToString(logs, Log::toString);
  }
}
