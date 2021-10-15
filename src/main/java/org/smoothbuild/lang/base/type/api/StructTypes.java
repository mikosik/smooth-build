package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class StructTypes {
  public static ImmutableMap<String, Integer> fieldsMap(ImmutableList<String> names) {
    Builder<String, Integer> builder = ImmutableMap.builder();
    for (int i = 0; i < names.size(); i++) {
      builder.put(names.get(i), i);
    }
    return builder.build();
  }

  public static boolean containsField(StructType type, String name) {
    return type.nameToIndex().containsKey(name);
  }

  public static Type fieldGet(StructType type, String name) {
    return type.fields().get(type.nameToIndex().get(name));
  }
}
