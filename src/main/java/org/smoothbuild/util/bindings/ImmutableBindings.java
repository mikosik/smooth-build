package org.smoothbuild.util.bindings;

import static java.util.stream.Collectors.joining;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

public final class ImmutableBindings<E> {
  private final ImmutableMap<String, E> bindings;

  public static <E> ImmutableBindings<E> immutableBindings() {
    return immutableBindings(ImmutableMap.of());
  }

  public static <E> ImmutableBindings<E> immutableBindings(ImmutableMap<String, ? extends E> map) {
    return new ImmutableBindings<>(map);
  }

  private ImmutableBindings(ImmutableMap<String, ? extends E> map) {
    // Cast is safe because ImmutableMap is immutable.
    @SuppressWarnings("unchecked")
    var castMap = (ImmutableMap<String, E>) map;
    this.bindings = castMap;
  }

  public OptionalBindings<E> newInnerScope() {
    return OptionalBindings.newOptionalBindings(this);
  }

  public E get(String name) {
    return getOpt(name).get();
  }

  public Optional<E> getOpt(String name) {
    return Optional.ofNullable(bindings.get(name));
  }

  public ImmutableMap<String, E> asMap() {
    return bindings;
  }

  @Override
  public String toString() {
    return prettyPrint(bindings);
  }

  private String prettyPrint(Map<String, E> bindings) {
    return bindings.values().stream()
        .map(s -> indent() + s)
        .collect(joining("\n"));
  }

  private String indent() {
    return "";
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ImmutableBindings<?> that
        && this.bindings.equals(that.bindings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bindings);
  }
}
