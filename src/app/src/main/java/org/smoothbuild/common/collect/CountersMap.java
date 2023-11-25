package org.smoothbuild.common.collect;

import static java.util.Collections.unmodifiableMap;
import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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

  public List<K> keysWithCounter(int value) {
    return listOfAll(counters.entrySet()).filter(e -> e.getValue() == value).map(Entry::getKey);
  }
}
