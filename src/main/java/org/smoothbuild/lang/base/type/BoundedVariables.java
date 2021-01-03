package org.smoothbuild.lang.base.type;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public record BoundedVariables(ImmutableMap<Variable, Bounds> boundsMap) {
  private static final BoundedVariables EMPTY = new BoundedVariables(ImmutableMap.of());

  public static BoundedVariables empty() {
    return EMPTY;
  }

  public BoundedVariables addBounds(Variable variable, Bounds bounds) {
    return mergeWith(ImmutableMap.of(variable, bounds));
  }

  public BoundedVariables mergeWith(BoundedVariables boundedVariables) {
    if (boundsMap.isEmpty()) {
      return boundedVariables;
    } else {
      return mergeWith(boundedVariables.boundsMap);
    }
  }

  private BoundedVariables mergeWith(ImmutableMap<Variable, Bounds> thatBoundsMap) {
    if (thatBoundsMap.isEmpty()) {
      return this;
    }

    Builder<Variable, Bounds> builder = ImmutableMap.builder();
    var thisIterator = this.boundsMap.entrySet().iterator();
    var thatIterator = thatBoundsMap.entrySet().iterator();
    var thisCurrent = nextOrNull(thisIterator);
    var thatCurrent = nextOrNull(thatIterator);

    while (thisCurrent != null && thatCurrent != null) {
      String thisName = thisCurrent.getKey().name();
      String thatName = thatCurrent.getKey().name();
      int comparison = thisName.compareTo(thatName);
      if (comparison < 0) {
        builder.put(thisCurrent);
        thisCurrent = nextOrNull(thisIterator);
      } else if (0 < comparison) {
        builder.put(thatCurrent);
        thatCurrent = nextOrNull(thatIterator);
      } else {
        builder.put(thisCurrent.getKey(), thisCurrent.getValue().mergeWith(thatCurrent.getValue()));
        thisCurrent = nextOrNull(thisIterator);
        thatCurrent = nextOrNull(thatIterator);
      }
    }
    while (thisCurrent != null) {
      builder.put(thisCurrent);
      thisCurrent = nextOrNull(thisIterator);
    }
    while (thatCurrent != null) {
      builder.put(thatCurrent);
      thatCurrent = nextOrNull(thatIterator);
    }
    return new BoundedVariables(builder.build());
  }

  private static Entry<Variable, Bounds> nextOrNull(Iterator<Entry<Variable, Bounds>> it) {
    return it.hasNext() ? it.next() : null;
  }

  public boolean areConsistent() {
    return boundsMap.values()
        .stream()
        .allMatch(Bounds::areConsistent);
  }
}
