package org.smoothbuild.util;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Streams.stream;

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
}
