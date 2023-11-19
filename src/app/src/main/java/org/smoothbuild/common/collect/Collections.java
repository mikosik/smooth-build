package org.smoothbuild.common.collect;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Collections {
  public static <T, V> Map<T, V> toMap(Collection<V> collection, Function<V, T> keyMapper) {
    return collection.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
  }

  /**
   * Converts Collection to map allowing null values (unlike Collectors.toMap).
   */
  public static <T, K, V> Map<K, V> toMap(
      Collection<T> collection, Function<T, K> keyMapper, Function<T, V> valueMapper) {
    return collection.stream()
        .collect(
            HashMap::new,
            (m, v) -> m.put(keyMapper.apply(v), valueMapper.apply(v)),
            HashMap::putAll);
  }
}
