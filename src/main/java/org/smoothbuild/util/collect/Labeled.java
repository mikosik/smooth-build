package org.smoothbuild.util.collect;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

public class Labeled<T> implements Nameable {
  private final Optional<String> label;
  private final T object;

  public static <E> Labeled<E> labeled(E object) {
    return new Labeled<>(Optional.empty(), object);
  }

  public static <E> Labeled<E> labeled(String name, E object) {
    return new Labeled<>(Optional.of(name), object);
  }

  public static <E> Labeled<E> labeled(Optional<String> name, E object) {
    return new Labeled<>(name, object);
  }

  protected Labeled(Optional<String> label, T object) {
    this.label = requireNonNull(label);
    this.object = requireNonNull(object);
  }

  @Override
  public Optional<String> nameO() {
    return label;
  }

  public T object() {
    return object;
  }

  public String saneLabel() {
    return nameO().orElse("");
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof Labeled<?> that
        && Objects.equals(label, that.label)
        && Objects.equals(this.object, that.object);
  }

  @Override
  public int hashCode() {
    return Objects.hash(label, object);
  }

  @Override
  public String toString() {
    return saneLabel() + "=" + object;
  }
}
