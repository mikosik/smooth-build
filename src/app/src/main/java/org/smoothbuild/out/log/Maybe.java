package org.smoothbuild.out.log;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.common.collect.Iterables.joinWithCommaToString;
import static org.smoothbuild.out.log.Level.ERROR;

import java.util.Objects;
import java.util.Optional;

public class Maybe<V> {
  private final V value;
  private final ImmutableLogs logs;

  public static <T> Maybe<T> maybe(T value, Log... logs) {
    var immutableLogs = ImmutableLogs.logs(logs);
    return maybe(value, immutableLogs);
  }

  public static <T> Maybe<T> maybe(T value, Logs logs) {
    var valueOrNull = logs.containsAtLeast(ERROR) ? null : value;
    return new Maybe<>(valueOrNull, logs);
  }

  public static <T> Maybe<T> success(T value, Log... logs) {
    return success(value, ImmutableLogs.logs(logs));
  }

  public static <T> Maybe<T> success(T value, Logs logs) {
    return new Maybe<>(value, logs);
  }

  public static <T> Maybe<T> failure(Log... logs) {
    return failure(ImmutableLogs.logs(logs));
  }

  public static <T> Maybe<T> failure(Logs logs) {
    return new Maybe<>(null, logs);
  }

  private Maybe(V value, Logs logs) {
    checkArgument((value == null) == logs.containsAtLeast(ERROR));
    this.value = value;
    this.logs = logs.toImmutableLogs();
  }

  public V value() {
    checkState(value != null, "No value is stored in this Maybe.");
    return value;
  }

  public Optional<V> valueOptional() {
    return Optional.ofNullable(value);
  }

  public ImmutableLogs logs() {
    return logs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Maybe<?> that) {
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
    return "Maybe{" + value + ", " + logsToString() + '}';
  }

  private String logsToString() {
    return "[" + joinWithCommaToString(logs.toList()) + "]";
  }
}
