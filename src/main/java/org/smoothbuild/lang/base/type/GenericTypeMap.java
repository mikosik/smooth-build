package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.Lists.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

public class GenericTypeMap {
  private final Map<GenericBasicType, Type> map;

  public static GenericTypeMap inferMapping(List<? extends Type> types, List<Type> actualTypes) {
    return new GenericTypeMap(inferMap(types, actualTypes));
  }

  private GenericTypeMap(Map<GenericBasicType, Type> map) {
    this.map = map;
  }

  public List<Type> applyTo(List<Type> types) {
    return map(types, this::applyTo);
  }

  public Type applyTo(Type type) {
    return type.mapTypeParameters(map);
  }

  private static Map<GenericBasicType, Type> inferMap(
      List<? extends Type> types, List<Type> actualTypes) {
    var builder = new HashMap<GenericBasicType, Type>();
    for (int i = 0; i < types.size(); i++) {
      Type current = types.get(i);
      var inferredMap = current.inferTypeParametersMap(actualTypes.get(i));
      for (var entry : inferredMap.entrySet()) {
        GenericBasicType key = entry.getKey();
        Type value = entry.getValue();
        if (builder.containsKey(key)) {
          Type previous = builder.get(key);
          Optional<Type> commonSuperType = previous.commonSuperType(value);
          builder.put(key,
              commonSuperType.orElseThrow(() -> noCommonSuperTypeException(value, previous)));
        } else {
          builder.put(key, value);
        }
      }
    }
    return ImmutableMap.copyOf(builder);
  }

  private static <T extends Type> IllegalArgumentException noCommonSuperTypeException(
      T type1, T type2) {
    return new IllegalArgumentException("Types " + type2.name() + ", " + type1.name()
        + " don't have common super type.");
  }
}
