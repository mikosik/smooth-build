package org.smoothbuild.common.collect;

import static com.google.common.collect.Maps.filterKeys;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.smoothbuild.common.function.Function1;

public final class Map<K, V> implements java.util.Map<K, V> {
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
    if (map instanceof Map<K, V> customMap) {
      return customMap;
    } else {
      return new Map<>(ImmutableMap.copyOf(map));
    }
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

  public Map<K, V> overrideWith(java.util.Map<? extends K, ? extends V> map) {
    return new Map<>(ImmutableMap.<K, V>builder()
        .putAll(map)
        .putAll(filterKeys(this, key -> !map.containsKey(key)))
        .build());
  }

  // Methods from java.util.Map

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return map.entrySet();
  }

  @Override
  public ImmutableSet<K> keySet() {
    return map.keySet();
  }

  @Override
  public ImmutableCollection<V> values() {
    return map.values();
  }

  @Override
  public V remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    map.forEach(action);
  }

  @Override
  public boolean remove(Object key, Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V put(K k, V v) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V putIfAbsent(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean replace(K key, V oldValue, V newValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V replace(K key, V value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V computeIfPresent(
      K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V compute(
      K key,
      BiFunction<
              ? super K,
              ? super @org.checkerframework.checker.nullness.qual.Nullable V,
              ? extends V>
          remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(java.util.Map<? extends K, ? extends V> map) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return map.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return map.containsValue(value);
  }

  @Override
  public V get(Object key) {
    return map.get(key);
  }

  @Override
  public V getOrDefault(Object key, V defaultValue) {
    return map.getOrDefault(key, defaultValue);
  }

  @Override
  public boolean equals(Object object) {
    return map.equals(object);
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
