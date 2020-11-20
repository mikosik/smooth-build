package org.smoothbuild.lang.base.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

public class InferTypeVariables {
  public static Map<TypeVariable, Type> inferTypeVariables(
      List<Type> types, List<Type> actualTypes) {
    var builder = new HashMap<TypeVariable, Type>();
    for (int i = 0; i < types.size(); i++) {
      for (var entry : types.get(i).inferTypeVariables(actualTypes.get(i)).entrySet()) {
        TypeVariable key = entry.getKey();
        Type value = entry.getValue();
        if (builder.containsKey(key)) {
          Type previous = builder.get(key);
          Optional<Type> leastUpperBound = previous.leastUpperBound(value);
          builder.put(key,
              leastUpperBound.orElseThrow(() -> noLeastUpperBoundException(value, previous)));
        } else {
          builder.put(key, value);
        }
      }
    }
    return ImmutableMap.copyOf(builder);
  }

  private static <T extends Type> IllegalArgumentException noLeastUpperBoundException(
      T type1, T type2) {
    return new IllegalArgumentException("Types " + type2.name() + ", " + type1.name()
        + " don't have least-upper-bound.");
  }
}
