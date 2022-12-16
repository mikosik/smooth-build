package org.smoothbuild.util.bindings;

import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

public final class ImmutableBindings<E> extends AbstractBindings<E> {
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

  @Override
  public Optional<E> getOptional(String name) {
    return Optional.ofNullable(bindings.get(name));
  }

  @Override
  public ImmutableMap<String, E> asMap() {
    return bindings;
  }

  @Override
  public String toString() {
    return bindingsToString(asMap());
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
