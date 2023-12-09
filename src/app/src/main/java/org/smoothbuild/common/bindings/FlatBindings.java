package org.smoothbuild.common.bindings;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maps.mapValues;
import static org.smoothbuild.common.collect.Maybe.maybe;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;

public sealed class FlatBindings<E> extends AbstractBindings<E>
    permits FlatImmutableBindings, FlatMutableBindings {
  protected final Map<String, E> map;

  protected FlatBindings(Map<String, E> map) {
    this.map = requireNonNull(map);
  }

  @Override
  public Maybe<E> getMaybe(String name) {
    return maybe(map.get(name));
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
      return listOfAll(map.entrySet())
          .map(e -> e.getKey() + " -> " + e.getValue())
          .toString("\n");
    }
  }
}
