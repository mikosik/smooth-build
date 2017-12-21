package org.smoothbuild.lang.type;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Math.max;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.type.ArrayType.arrayWithDepth;
import static org.smoothbuild.lang.type.Types.NOTHING;

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
    int nothingMaxDepth = -1;
    for (Type type : uniqueTypes) {
      if (type.coreType().isNothing()) {
        nothingMaxDepth = max(nothingMaxDepth, type.coreDepth());
      } else {
        for (Type t : type.hierarchy()) {
          if (!alreadySorted.contains(t)) {
            sorted.add(t);
            alreadySorted.add(t);
          }
        }
      }
    }
    for (int i = nothingMaxDepth; 0 <= i; i--) {
      sorted.add(arrayWithDepth(NOTHING, i));
    }
    return sorted
        .stream()
        .filter(t -> uniqueTypes.contains(t))
        .collect(toList());
  }
}
