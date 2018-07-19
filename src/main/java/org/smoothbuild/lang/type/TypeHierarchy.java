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
  public static List<Type> sortedTypes(Iterable<? extends Type> types) {
    Set<Type> uniqueTypes = newHashSet(types);
    List<Type> sorted = new ArrayList<>();
    Set<Type> alreadySorted = new HashSet<>();
    Type nothingArray = null;
    for (Type type : uniqueTypes) {
      if (type.coreType().isNothing()) {
        if (nothingArray == null || nothingArray.coreDepth() < type.coreDepth()) {
          nothingArray = type;
        }
      } else {
        for (Type t : type.hierarchy()) {
          if (!alreadySorted.contains(t)) {
            sorted.add(t);
            alreadySorted.add(t);
          }
        }
      }
    }
    if (nothingArray != null) {
      while (nothingArray instanceof ArrayType) {
        sorted.add(nothingArray);
        nothingArray = ((ArrayType) nothingArray).elemType();
      }
      sorted.add(nothingArray);
    }
    return sorted
        .stream()
        .filter(t -> uniqueTypes.contains(t))
        .collect(toList());
  }
}
