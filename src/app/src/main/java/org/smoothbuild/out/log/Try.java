package org.smoothbuild.out.log;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.out.log.Log.containsAnyFailure;

import java.util.Collection;
import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;

public class Try<V> {
  private final V value;
  private final List<Log> logs;

  public static <T> Try<T> of(T value, Log... logs) {
    return Try.of(value, list(logs));
  }

  public static <T> Try<T> of(T value, Collection<Log> logs) {
    var valueOrNull = containsAnyFailure(logs) ? null : value;
    return new Try<>(valueOrNull, logs);
  }

  public static <T> Try<T> success(T value, Log... logs) {
    return success(value, list(logs));
  }

  public static <T> Try<T> success(T value, Collection<Log> logs) {
    return new Try<>(value, logs);
  }

  public static <T> Try<T> failure(Log... logs) {
    return failure(list(logs));
  }

  public static <T> Try<T> failure(Collection<Log> logs) {
    return new Try<>(null, logs);
  }

  private Try(V value, Collection<Log> logs) {
    checkArgument((value == null) == containsAnyFailure(logs));
    this.value = value;
    this.logs = listOfAll(logs);
  }

  public V value() {
    checkState(value != null, "No value is stored in this Try.");
    return value;
  }

  public Maybe<V> toMaybe() {
    return maybe(value);
  }

  public List<Log> logs() {
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
    return logs.toString("[", ",", "]");
  }
}
