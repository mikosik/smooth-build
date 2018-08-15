package org.smoothbuild.lang.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class InferTypes {
  public static <T extends Type> Map<GenericType, T> inferActualCoreTypes(
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
