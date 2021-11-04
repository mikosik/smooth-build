package org.smoothbuild.lang.base.type.api;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import com.google.common.collect.ImmutableMap;

public record BoundsMap<T extends Type>(ImmutableMap<Variable, Bounded<T>> map) {
  private static final BoundsMap<?> EMPTY = new BoundsMap<>(ImmutableMap.of());

  public static <R extends Type> BoundsMap<R> boundsMap() {
    return (BoundsMap<R>) EMPTY;
  }

  public static <R extends Type> BoundsMap<R> boundsMap(Bounded<R> bounded) {
    return new BoundsMap<>(ImmutableMap.of(bounded.variable(), bounded));
  }

  @Override
  public String toString() {
    return toCommaSeparatedString(map.values());
  }
}
