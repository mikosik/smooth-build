package org.smoothbuild.lang.type;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeHierarchy {

  /**
   * @return types sorted using type - subtype relationship. Each type comes before all of its
   *         subtypes.
   */
  public static List<ConcreteType> sortedTypes(Iterable<? extends ConcreteType> types) {
    Set<ConcreteType> uniqueTypes = newHashSet(types);
    List<ConcreteType> sorted = new ArrayList<>();
    Set<ConcreteType> alreadySorted = new HashSet<>();
    ConcreteType nothingArray = null;
    for (ConcreteType type : uniqueTypes) {
      if (type.coreType().isNothing()) {
        if (nothingArray == null || nothingArray.coreDepth() < type.coreDepth()) {
          nothingArray = type;
        }
      } else {
        for (ConcreteType t : type.hierarchy()) {
          if (!alreadySorted.contains(t)) {
            sorted.add(t);
            alreadySorted.add(t);
          }
        }
      }
    }
    if (nothingArray != null) {
      while (nothingArray instanceof ConcreteArrayType) {
        sorted.add(nothingArray);
        nothingArray = ((ConcreteArrayType) nothingArray).elemType();
      }
      sorted.add(nothingArray);
    }
    return sorted
        .stream()
        .filter(t -> uniqueTypes.contains(t))
        .collect(toList());
  }
}
