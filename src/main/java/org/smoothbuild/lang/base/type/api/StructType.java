package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface StructType extends Type {
  public ImmutableList<Type> fields();

  public ImmutableMap<String, Integer> nameToIndex();

  public static Type fieldGet(StructType type, String name) {
    return type.fields().get(type.nameToIndex().get(name));
  }

  public static boolean containsField(StructType type, String name) {
    return type.nameToIndex().containsKey(name);
  }
}
