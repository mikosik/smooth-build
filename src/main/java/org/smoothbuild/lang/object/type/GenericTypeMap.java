package org.smoothbuild.lang.object.type;

import static org.smoothbuild.util.Lists.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class GenericTypeMap<T extends Type> {
  private final Map<GenericType, T> map;

  public static <T extends Type> GenericTypeMap<T> inferMapping(List<? extends Type> types,
      List<T> actualTypes) {
    return new GenericTypeMap<>(inferMap(types, actualTypes));
  }

  private GenericTypeMap(Map<GenericType, T> map) {
    this.map = map;
  }

  public List<T> applyTo(List<Type> types) {
    return map(types, this::applyTo);
  }

  public T applyTo(Type type) {
    if (type.isGeneric()) {
      return type.replaceCoreType(map.get(type.coreType()));
    } else {
      @SuppressWarnings("unchecked")
      T result = (T) type;
      return result;
    }
  }

  private static <T extends Type> Map<GenericType, T> inferMap(
      List<? extends Type> types, List<T> actualTypes) {
    Map<GenericType, T> builder = new HashMap<>();
    for (int i = 0; i < types.size(); i++) {
      Type current = types.get(i);
      if (current.isGeneric()) {
        GenericType type = (GenericType) current;
        GenericType core = type.coreType();
        T actualCore = type.actualCoreTypeWhenAssignedFrom(actualTypes.get(i));
        if (builder.containsKey(core)) {
          T previous = builder.get(core);
          @SuppressWarnings("unchecked")
          T commonSuperType = (T) previous.commonSuperType(actualCore);
          if (commonSuperType == null) {
            throw new IllegalArgumentException("Types " + previous.name() + ", " + actualCore.name()
                + " don't have common super type.");
          } else {
            builder.put(core, commonSuperType);
          }
        } else {
          builder.put(core, actualCore);
        }
      }
    }
    return ImmutableMap.copyOf(builder);
  }
}
