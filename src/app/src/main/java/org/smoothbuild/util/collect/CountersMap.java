package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

public class CountersMap<K> {
  private final Map<K, Integer> counters = new HashMap<>();

  public int count(K key) {
    return counters.getOrDefault(key, 0);
  }

  public void increment(K key) {
    int incrementedCount = count(key) + 1;
    counters.put(key, incrementedCount);
  }

  public Map<K, Integer> counters() {
    return unmodifiableMap(counters);
  }

  public ImmutableList<K> keysWithCounter(int value) {
    return counters.entrySet()
        .stream()
        .filter(e -> e.getValue() == value)
        .map(Entry::getKey)
        .collect(toImmutableList());
  }
}
