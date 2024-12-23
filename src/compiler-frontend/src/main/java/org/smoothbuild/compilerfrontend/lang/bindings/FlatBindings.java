package org.smoothbuild.compilerfrontend.lang.bindings;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.mapOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;

import java.util.Objects;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.function.Function1;

public sealed class FlatBindings<E> extends AbstractBindings<E>
    permits FlatImmutableBindings, FlatMutableBindings {
  protected final java.util.Map<String, E> map;

  protected FlatBindings(java.util.Map<String, E> map) {
    this.map = requireNonNull(map);
  }

  @Override
  public Maybe<E> getMaybe(String name) {
    return maybe(map.get(name));
  }

  @Override
  public <F, T extends Throwable> FlatBindings<F> map(Function1<? super E, F, T> mapper) throws T {
    return new FlatBindings<>(mapOfAll(map).mapValues(mapper));
  }

  @Override
  public Map<String, E> toMap() {
    return mapOfAll(map);
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
