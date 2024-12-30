package org.smoothbuild.compilerfrontend.lang.bindings;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Map.mapOfAll;

import java.util.Objects;
import org.smoothbuild.common.collect.Map;

public sealed class FlatBindings<E> implements Bindings<E>
    permits ImmutableFlatBindings, MutableFlatBindings {
  protected final java.util.Map<String, E> map;

  protected FlatBindings(java.util.Map<String, E> map) {
    this.map = requireNonNull(map);
  }

  @Override
  public E get(String name) {
    return map.get(name);
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
