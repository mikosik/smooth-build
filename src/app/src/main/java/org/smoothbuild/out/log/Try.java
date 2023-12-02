package org.smoothbuild.out.log;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.common.collect.Iterables.joinWithCommaToString;
import static org.smoothbuild.common.option.Maybe.maybe;
import static org.smoothbuild.out.log.Level.ERROR;

import java.util.Objects;
import org.smoothbuild.common.option.Maybe;

public class Try<V> {
  private final V value;
  private final ImmutableLogs logs;

  public static <T> Try<T> of(T value, Log... logs) {
    var immutableLogs = ImmutableLogs.logs(logs);
    return Try.of(value, immutableLogs);
  }

  public static <T> Try<T> of(T value, Logs logs) {
    var valueOrNull = logs.containsAtLeast(ERROR) ? null : value;
    return new Try<>(valueOrNull, logs);
  }

  public static <T> Try<T> success(T value, Log... logs) {
    return success(value, ImmutableLogs.logs(logs));
  }

  public static <T> Try<T> success(T value, Logs logs) {
    return new Try<>(value, logs);
  }

  public static <T> Try<T> failure(Log... logs) {
    return failure(ImmutableLogs.logs(logs));
  }

  public static <T> Try<T> failure(Logs logs) {
    return new Try<>(null, logs);
  }

  private Try(V value, Logs logs) {
    checkArgument((value == null) == logs.containsAtLeast(ERROR));
    this.value = value;
    this.logs = logs.toImmutableLogs();
  }

  public V value() {
    checkState(value != null, "No value is stored in this Try.");
    return value;
  }

  public Maybe<V> toMaybe() {
    return maybe(value);
  }

  public ImmutableLogs logs() {
    return logs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Try<?> that) {
      return Objects.equals(value, that.value) && this.logs.equals(that.logs);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, logs);
  }

  @Override
  public String toString() {
    return "Try{" + value + ", " + logsToString() + '}';
  }

  private String logsToString() {
    return "[" + joinWithCommaToString(logs.toList()) + "]";
  }
}
