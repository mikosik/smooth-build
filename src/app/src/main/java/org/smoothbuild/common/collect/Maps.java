package org.smoothbuild.common.collect;

import java.util.Map;
import org.smoothbuild.common.function.Function1;

public class Maps {
  /**
   * Works like Map.computeIfAbsent() but allows modifying map from mappingFunction and
   * use Function1 as mapper.
   */
  public static <K, V, E extends Throwable> V computeIfAbsent(
      Map<K, V> map, K key, Function1<? super K, ? extends V, E> mapper) throws E {
    V value = map.get(key);
    if (value == null) {
      value = mapper.apply(key);
      map.put(key, value);
    }
    return value;
  }
}
