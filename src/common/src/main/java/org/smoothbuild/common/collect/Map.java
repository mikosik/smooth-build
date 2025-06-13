package org.smoothbuild.common.collect;

import static com.google.common.collect.Maps.filterKeys;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Set.setOfAll;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import org.smoothbuild.common.function.Function1;

public final class Map<K, V> {
  private final ImmutableMap<K, V> map;

  public static <K, V> Map<K, V> map() {
    return new Map<>(ImmutableMap.of());
  }

  public static <K, V> Map<K, V> map(K k1, V v1) {
    return new Map<>(ImmutableMap.of(k1, v1));
  }

  public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2) {
    return new Map<>(ImmutableMap.of(k1, v1, k2, v2));
  }

  public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3) {
    return new Map<>(ImmutableMap.of(k1, v1, k2, v2, k3, v3));
  }

  public static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    return new Map<>(ImmutableMap.of(k1, v1, k2, v2, k3, v3, k4, v4));
  }

  public static <K, V> Map<K, V> mapOfAll(java.util.Map<K, V> map) {
    return new Map<>(ImmutableMap.copyOf(map));
  }

  public static <K, V> Map<K, V> zipToMap(Iterable<K> keys, Iterable<V> values) {
    Builder<K, V> builder = ImmutableMap.builder();
    var keysIterator = keys.iterator();
    var valuesIterator = values.iterator();
    while (keysIterator.hasNext() && valuesIterator.hasNext()) {
      builder.put(keysIterator.next(), valuesIterator.next());
    }
    if (keysIterator.hasNext()) {
      throw new IllegalArgumentException("keys have more elements than values");
    }
    if (valuesIterator.hasNext()) {
      throw new IllegalArgumentException("values have more elements than keys");
    }
    return new Map<>(builder.build());
  }

  private Map(ImmutableMap<K, V> map) {
    this.map = map;
  }

  public Set<Entry<K, V>> entrySet() {
    return setOfAll(map.entrySet());
  }

  public Set<K> keySet() {
    return setOfAll(map.keySet());
  }

  public Collection<V> values() {
    return listOfAll(map.values());
  }

  public Map<K, V> put(K key, V value) {
    var newMap = new HashMap<>(this.map);
    newMap.put(key, value);
    return new Map<>(ImmutableMap.copyOf(newMap));
  }

  public <K2, T extends Throwable> Map<K2, V> mapKeys(Function1<? super K, K2, T> mapper) throws T {
    return mapEntries(mapper, v -> v);
  }

  public <V2, T extends Throwable> Map<K, V2> mapValues(Function1<? super V, V2, T> mapper)
      throws T {
    return mapEntries(k -> k, mapper);
  }

  public <K2, V2, T1 extends Throwable, T2 extends Throwable> Map<K2, V2> mapEntries(
      Function1<? super K, K2, T1> keyFunction, Function1<? super V, V2, T2> valueFunction)
      throws T1, T2 {
    Builder<K2, V2> builder = ImmutableMap.builder();
    for (Entry<K, V> entry : map.entrySet()) {
      builder.put(keyFunction.apply(entry.getKey()), valueFunction.apply(entry.getValue()));
    }
    return new Map<>(builder.build());
  }

  public Map<K, V> overrideWith(Map<? extends K, ? extends V> map) {
    return new Map<>(ImmutableMap.<K, V>builder()
        .putAll(map.map)
        .putAll(filterKeys(this.map, key -> !map.map.containsKey(key)))
        .build());
  }

  public int size() {
    return map.size();
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  public V get(K key) {
    return map.get(key);
  }

  public V getOrDefault(Object key, V defaultValue) {
    return map.getOrDefault(key, defaultValue);
  }

  public java.util.Map<K, V> asJdkMap() {
    return map;
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Map<?, ?> that && Objects.equals(this.map, that.map);
  }

  @Override
  public int hashCode() {
    return map.hashCode();
  }

  @Override
  public String toString() {
    return map.toString();
  }

  public String toString(String prefix, String delimiter, String suffix) {
    return prefix + toString(delimiter) + suffix;
  }

  public String toString(String delimiter) {
    return String.join(delimiter, map.entrySet().stream().map(Object::toString).toList());
  }
}
