package org.smoothbuild.lang.type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class InferTypes {
  public static Map<GenericType, Type> inferActualCoreTypes(List<GenericType> types,
      List<Type> actualTypes) {
    Map<GenericType, Type> builder = new HashMap<>();
    for (int i = 0; i < types.size(); i++) {
      GenericType type = types.get(i);
      GenericType core = type.coreType();
      Type actualCore = type.actualCoreTypeWhenAssignedFrom(actualTypes.get(i));
      if (builder.containsKey(core)) {
        Type previous = builder.get(core);
        Type commonSuperType = previous.commonSuperType(actualCore);
        if (commonSuperType == null) {
          throw new IllegalArgumentException();
        } else {
          builder.put(core, commonSuperType);
        }
      } else {
        builder.put(core, actualCore);
      }
    }
    return ImmutableMap.copyOf(builder);
  }
}
