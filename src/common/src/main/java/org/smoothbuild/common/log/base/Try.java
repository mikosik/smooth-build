package org.smoothbuild.common.log.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.Log.containsAnyFailure;

import java.util.Collection;
import java.util.Objects;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;

public class Try<V> {
  private final Maybe<V> value;
  private final List<Log> logs;

  public static <T> Try<T> of(T value, Log... logs) {
    return of(value, list(logs));
  }

  public static <T> Try<T> of(T value, Collection<Log> logs) {
    Maybe<T> maybe = containsAnyFailure(logs) ? Maybe.none() : some(value);
    return new Try<>(maybe, logs);
  }

  public static <T> Try<T> success(T value, Log... logs) {
    return success(value, list(logs));
  }

  public static <T> Try<T> success(T value, Collection<Log> logs) {
    return new Try<>(some(value), logs);
  }

  public static <T> Try<T> failure(Log... logs) {
    return failure(list(logs));
  }

  public static <T> Try<T> failure(Collection<Log> logs) {
    return new Try<>(none(), logs);
  }

  private Try(Maybe<V> value, Collection<Log> logs) {
    checkArgument((value.isNone()) == containsAnyFailure(logs));
    this.value = value;
    this.logs = listOfAll(logs);
  }

  public V value() {
    checkState(value.isSome(), "No value is stored in this Try.");
    return value.get();
  }

  public Maybe<V> toMaybe() {
    return value;
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
