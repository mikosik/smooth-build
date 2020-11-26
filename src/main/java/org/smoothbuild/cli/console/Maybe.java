package org.smoothbuild.cli.console;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.Objects;

public class Maybe<V> extends MemoryLogger {
  private V value;

  public Maybe() {
    this.value = null;
  }

  public static <T> Maybe<T> of(T value) {
    return new Maybe<>(value);
  }

  public static <T> Maybe<T> withLogsFrom(MemoryLogger logger) {
    return new Maybe<>(logger);
  }

  private Maybe(V value) {
    this.value = value;
  }

  private Maybe(MemoryLogger logger) {
    super(logger);
    this.value = null;
  }

  public void setValue(V value) {
    this.value = value;
  }

  public V value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Maybe<?> that) {
      return Objects.equals(value, that.value) && this.logs().equals(that.logs());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, logs());
  }

  @Override
  public String toString() {
    return "Maybe{" + value + ", " + logsToString() + '}';
  }

  private String logsToString() {
    var elements = logs().stream().map(Object::toString).collect(toImmutableList());
    return "[" + String.join(", ", elements) + "]";
  }
}
