package org.smoothbuild.lang.base.type;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public record BoundsMap(ImmutableMap<Variable, Bounded> map) {
  private static final BoundsMap EMPTY = new BoundsMap(ImmutableMap.of());

  public static BoundsMap boundsMap() {
    return EMPTY;
  }

  public static BoundsMap boundsMap(Bounded bounded) {
    return new BoundsMap(ImmutableMap.of(bounded.variable(), bounded));
  }

  public static BoundsMap merge(Iterable<BoundsMap> iterable) {
    var result = new HashMap<Variable, Bounded>();
    for (BoundsMap boundsMap : iterable) {
      mergeToMap(result, boundsMap.map().values());
    }
    return toBoundedVariables(result);
  }

  public BoundsMap mergeWith(Iterable<Bounded> boundeds) {
    var result = new HashMap<>(map);
    mergeToMap(result, boundeds);
    return toBoundedVariables(result);
  }

  private static void mergeToMap(Map<Variable, Bounded> map, Iterable<Bounded> boundeds) {
    for (Bounded bounded : boundeds) {
      map.merge(bounded.variable(), bounded, Bounded::mergeWith);
    }
  }

  private static BoundsMap toBoundedVariables(Map<Variable, Bounded> map) {
    return new BoundsMap(ImmutableMap.copyOf(map));
  }

  public boolean areConsistent() {
    return map.values()
        .stream()
        .allMatch(b -> b.bounds().areConsistent());
  }
}
