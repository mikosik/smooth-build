package org.smoothbuild.util.collect;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.mapValues;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;

public class Optionals {
  public static <T, S, R> Optional<R> flatMapPair(Optional<T> first, Optional<S> second,
      BiFunction<T, S, Optional<R>> biFunction) {
    return first.flatMap(f -> second.flatMap(s -> biFunction.apply(f, s)));
  }

  public static <T> Optional<ImmutableList<T>> pullUp(
      Iterable<? extends Optional<? extends T>> iterable) {
    if (Streams.stream(iterable).anyMatch(Optional::isEmpty)) {
      return Optional.empty();
    } else {
      return Optional.of(map(iterable, Optional::get));
    }
  }

  public static <K, V> Optional<ImmutableMap<K, V>> pullUp(
      Map<K, ? extends Optional<? extends V>> map) {
    if (map.values().stream().anyMatch(Optional::isEmpty)) {
      return Optional.empty();
    } else {
      return Optional.of(mapValues(map, Optional::get));
    }
  }
}
