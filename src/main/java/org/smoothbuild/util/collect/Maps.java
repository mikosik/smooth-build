package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import org.smoothbuild.util.function.ThrowingFunction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

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

  public static <T, U> ImmutableMap<T, U> concat(
      ImmutableMap<? extends T, ? extends U> map1,
      ImmutableMap<? extends T, ? extends U> map2) {
    return ImmutableMap.<T, U>builder()
        .putAll(map1)
        .putAll(filterKeys(map2, key -> !map1.containsKey(key)))
        .build();
  }

  public static <K, V, K2> ImmutableMap<K2, V> mapKeys(Map<K, V> map,
      Function<? super K, K2> keysFunction) {
    return mapEntries(map, keysFunction, v -> v);
  }

  public static <K, V, V2> ImmutableMap<K, V2> mapValues(Map<K, V> map,
      Function<? super V, V2> valueFunction) {
    return mapEntries(map, k -> k, valueFunction);
  }

  public static <K, V, K2, V2> ImmutableMap<K2, V2> mapEntries(Map<K, V> map,
      Function<? super K, K2> keyFunction, Function<? super V, V2> valueFunction) {
    return map.entrySet().stream()
        .collect(toImmutableMap(
            e -> keyFunction.apply(e.getKey()),
            e -> valueFunction.apply(e.getValue())));
  }

  public static <K, V, K2, V2> ImmutableMap<K2, V2> mapEntries(Map<K, V> map,
      Function<? super Map.Entry<K, V>, Map.Entry<K2, V2>> entryFunction) {
    var mappedEntrySet = map.entrySet()
        .stream()
        .map(entryFunction)
        .collect(toList());
    return ImmutableMap.copyOf(mappedEntrySet);
  }

  /**
   * Works like Map.computeIfAbsent() but allows modifying map from mappingFunction and
   * use ThrowingFunction as mappingFunction.
   */
  public static <K, V, E extends Throwable> V computeIfAbsent(Map<K, V> map, K key,
      ThrowingFunction<? super K, ? extends V, E> mappingFunction) throws E {
    V value = map.get(key);
    if (value == null) {
      value = mappingFunction.apply(key);
      map.put(key, value);
    }
    return value;
  }

  public static <K, V> ImmutableMap<K, V> sort(Map<K, V> map,
      Comparator<Entry<K, V>> comparator) {
    return map.entrySet()
        .stream()
        .sorted(comparator)
        .collect(toImmutableMap(Entry::getKey, Entry::getValue));
  }

  public static <K, V> ImmutableMap<K, V> zip(List<? extends K> keys, List<? extends V> values) {
    if (keys.size() != values.size()) {
      throw new IllegalArgumentException(
          "List sizes differ " + keys.size() + " != " + values.size() + ".");
    }
    Builder<K, V> builder = ImmutableMap.builder();
    for (int i = 0; i < keys.size(); i++) {
      builder.put(keys.get(i), values.get(i));
    }
    return builder.build();
  }
}
