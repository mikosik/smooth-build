package org.smoothbuild.util;

import java.util.HashMap;
import java.util.Map;

public class CountersMap<K> {
  private final Map<K, Integer> counters = new HashMap<>();

  public int count(K key) {
    return counters.getOrDefault(key, 0);
  }

  public void increment(K key) {
    int incrementedCount = count(key) + 1;
    counters.put(key, incrementedCount);
  }
}
