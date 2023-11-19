package org.smoothbuild.common.bindings;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.Iterables.joinToString;
import static org.smoothbuild.common.collect.Maps.mapValues;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public sealed class FlatBindings<E> extends AbstractBindings<E>
    permits FlatImmutableBindings, FlatMutableBindings {
  protected final Map<String, E> map;

  protected FlatBindings(Map<String, E> map) {
    this.map = requireNonNull(map);
  }

  @Override
  public Optional<E> getOptional(String name) {
    return Optional.ofNullable(map.get(name));
  }

  @Override
  public <T> FlatBindings<T> map(Function<? super E, T> mapper) {
    return new FlatBindings<>(mapValues(map, mapper));
  }

  @Override
  public ImmutableMap<String, E> toMap() {
    return ImmutableMap.copyOf(map);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof FlatBindings<?> that && Objects.equals(this.map, that.map);
  }

  @Override
  public int hashCode() {
    return Objects.hash(map);
  }

  @Override
  public String toString() {
    if (map.isEmpty()) {
      return "<no bindings>";
    } else {
      return joinToString(map.entrySet(), e -> e.getKey() + " -> " + e.getValue(), "\n");
    }
  }
}
