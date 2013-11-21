package org.smoothbuild.lang.function.def.args;

import java.util.Set;

import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class Helpers {

  public static <T> ImmutableMap<Type, Set<T>> createMap(Set<Type> types) {
    ImmutableMap.Builder<Type, Set<T>> builder = ImmutableMap.builder();
    for (Type type : types) {
      builder.put(type, Sets.<T> newHashSet());
    }
    return builder.build();
  }
}
