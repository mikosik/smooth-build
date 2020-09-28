package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.Lists.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

public class GenericTypeMap {
  private final Map<Type, Type> map;

  public static GenericTypeMap inferMapping(List<? extends Type> types, List<Type> actualTypes) {
    return new GenericTypeMap(inferMap(types, actualTypes));
  }

  private GenericTypeMap(Map<Type, Type> map) {
    this.map = map;
  }

  public List<Type> applyTo(List<Type> types) {
    return map(types, this::applyTo);
  }

  public Type applyTo(Type type) {
    if (type.isGeneric()) {
      return type.mapTypeParameters(map);
    } else {
      return type;
    }
  }

  private static Map<Type, Type> inferMap(
      List<? extends Type> types, List<Type> actualTypes) {
    Map<Type, Type> builder = new HashMap<>();
    for (int i = 0; i < types.size(); i++) {
      Type current = types.get(i);
      if (current.isGeneric()) {
        Type core = current.coreType();
        Type actualCore = current.actualCoreTypeWhenAssignedFrom(actualTypes.get(i));
        if (builder.containsKey(core)) {
          Type previous = builder.get(core);
          Optional<Type> commonSuperType = previous.commonSuperType(actualCore);
          builder.put(core,
              commonSuperType.orElseThrow(() -> noCommonSuperTypeException(actualCore, previous)));
        } else {
          builder.put(core, actualCore);
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
