package org.smoothbuild.lang.base.type;

import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public record VariableToBounds(ImmutableMap<TypeVariable, Bounds> boundsMap) {
  private static final VariableToBounds EMPTY = new VariableToBounds(ImmutableMap.of());

  public static VariableToBounds empty() {
    return EMPTY;
  }

  public VariableToBounds addBounds(TypeVariable variable, Bounds bounds) {
    return mergeWith(ImmutableMap.of(variable, bounds));
  }

  public VariableToBounds mergeWith(VariableToBounds variableToBounds) {
    if (boundsMap.isEmpty()) {
      return variableToBounds;
    } else {
      return mergeWith(variableToBounds.boundsMap);
    }
  }

  private VariableToBounds mergeWith(ImmutableMap<TypeVariable, Bounds> thatBoundsMap) {
    if (thatBoundsMap.isEmpty()) {
      return this;
    }

    Builder<TypeVariable, Bounds> builder = ImmutableMap.builder();
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
    return new VariableToBounds(builder.build());
  }

  private static Entry<TypeVariable, Bounds> nextOrNull(Iterator<Entry<TypeVariable, Bounds>> it) {
    return it.hasNext() ? it.next() : null;
  }

  public boolean areConsistent() {
    return boundsMap.values()
        .stream()
        .allMatch(Bounds::areConsistent);
  }
}
