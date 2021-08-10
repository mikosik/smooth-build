package org.smoothbuild.util;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;

public class Maps {
  public static <K, V> ImmutableMap<K, V> toMap(Iterable<K> keys,
      Function<? super K, V> valueFunction) {
    return stream(keys)
        .collect(toImmutableMap(o -> o, valueFunction));
  }

  public static <E, K, V> ImmutableMap<K, V> toMap(Iterable<E> keys,
      Function<? super E, K> keyFunction, Function<? super E, V> valueFunction) {
    return stream(keys)
        .collect(toImmutableMap(keyFunction, valueFunction));
  }

  public static <K, V, V2> ImmutableMap<K, V2> mapValues(Map<K, V> map,
      Function<? super V, V2> valueFunction) {
    return map(map, k -> k, valueFunction);
  }

  public static <K, V, K2, V2> ImmutableMap<K2, V2> map(Map<K, V> map,
      Function<? super K, K2> keyFunction, Function<? super V, V2> valueFunction) {
    return map.entrySet().stream()
        .collect(toImmutableMap(
            e -> keyFunction.apply(e.getKey()),
            e -> valueFunction.apply(e.getValue())));
  }
}
