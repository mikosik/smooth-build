package org.smoothbuild.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Collections {
  public static <T, V> Map<T, V> toMap(Collection<V> collection, Function<V, T> keyMapper) {
    return collection
        .stream()
        .collect(Collectors.toMap(keyMapper, Function.identity()));
  }
}
