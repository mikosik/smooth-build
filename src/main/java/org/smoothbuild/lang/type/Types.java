package org.smoothbuild.lang.type;

import static com.google.common.collect.Lists.reverse;

import java.util.List;

public class Types {
  public static Type closestCommonConvertibleTo(Type type1, Type type2) {
    /*
     * Algorithm below works correctly for all smooth types currently existing in smooth but doesn't
     * work when it is possible to define user struct types. It will fail when conversion chain
     * (hierarchy) contains cycle (for example struct type is convertible to itself) or conversion
     * chain has infinite length (for example structure X is convertible to its array [X]). This
     * comment will become obsolete once we get rid of conversion chains (and direct-convertible-to)
     * and instead create normal object oriented type hierarchy with one root (Value type).
     */
    List<Type> hierarchy1 = type1.hierarchy();
    List<Type> hierarchy2 = type2.hierarchy();
    Type type = closesCommonSuperType(hierarchy1, hierarchy2);
    if (type == null) {
      Type last1 = hierarchy1.get(0);
      Type last2 = hierarchy2.get(0);
      boolean isNothing1 = last1.coreType().isNothing();
      boolean isNothing2 = last2.coreType().isNothing();
      if (isNothing1 && isNothing2) {
        type = last1.coreDepth() < last2.coreDepth() ? last2 : last1;
      } else if (isNothing1) {
        type = firstWithDepthNotLowerThan(hierarchy2, last1.coreDepth());
      } else if (isNothing2) {
        type = firstWithDepthNotLowerThan(hierarchy1, last2.coreDepth());
      }
    }
    return type;
  }

  private static Type closesCommonSuperType(List<Type> hierarchy1, List<Type> hierarchy2) {
    int index = 0;
    Type type = null;
    while (index < hierarchy1.size() && index < hierarchy2.size()
        && hierarchy1.get(index).equals(hierarchy2.get(index))) {
      type = hierarchy1.get(index);
      index++;
    }
    return type;
  }

  private static Type firstWithDepthNotLowerThan(List<Type> hierarchy, int depth) {
    return reverse(hierarchy)
        .stream()
        .filter(t -> depth <= t.coreDepth())
        .findFirst()
        .orElse(null);
  }
}
