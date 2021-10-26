package org.smoothbuild.util.collect;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

public class Named<T> {
  private final Optional<String> name;
  private final T object;

  public static <E> Named<E> named(E object) {
    return new Named<>(Optional.empty(), object);
  }

  public static <E> Named<E> named(String name, E object) {
    return new Named<>(Optional.of(name), object);
  }

  public static <E> Named<E> named(Optional<String> name, E object) {
    return new Named<>(name, object);
  }

  protected Named(Optional<String> name, T object) {
    this.name = requireNonNull(name);
    this.object = requireNonNull(object);
  }

  public Optional<String> name() {
    return name;
  }

  public T object() {
    return object;
  }

  /**
   * @return name of this parameter inside backticks.
   */
  public String q() {
    return "`" + saneName() + "`";
  }

  public String saneName() {
    return name().orElse("");
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Named<?> that
        && Objects.equals(name, that.name)
        && Objects.equals(this.object, that.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, object);
  }

  @Override
  public String toString() {
    return saneName() + "=" + object;
  }
}
