package org.smoothbuild.lang.function.def.args;

import java.util.Set;

import org.smoothbuild.lang.base.SType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class Helpers {

  public static <T> ImmutableMap<SType<?>, Set<T>> createMap(Set<SType<?>> types) {
    ImmutableMap.Builder<SType<?>, Set<T>> builder = ImmutableMap.builder();
    for (SType<?> type : types) {
      builder.put(type, Sets.<T> newHashSet());
    }
    return builder.build();
  }
}
