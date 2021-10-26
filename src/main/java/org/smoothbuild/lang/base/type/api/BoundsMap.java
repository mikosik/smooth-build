package org.smoothbuild.lang.base.type.api;

import static org.smoothbuild.util.collect.Lists.toCommaSeparatedString;

import com.google.common.collect.ImmutableMap;

public record BoundsMap(ImmutableMap<Variable, Bounded> map) {
  private static final BoundsMap EMPTY = new BoundsMap(ImmutableMap.of());

  public static BoundsMap boundsMap() {
    return EMPTY;
  }

  public static BoundsMap boundsMap(Bounded bounded) {
    return new BoundsMap(ImmutableMap.of(bounded.variable(), bounded));
  }

  @Override
  public String toString() {
    return toCommaSeparatedString(map.values());
  }
}
