package org.smoothbuild.common.collect;

import static org.smoothbuild.common.collect.Lists.map;
import static org.smoothbuild.common.collect.Maps.mapValues;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class Optionals {
  public static <T, S, R> Optional<R> mapPair(
      Optional<T> first, Optional<S> second, BiFunction<T, S, R> biFunction) {
    return first.flatMap(f -> second.map(s -> biFunction.apply(f, s)));
  }

  public static <T, S, R> Optional<R> flatMapPair(
      Optional<T> first, Optional<S> second, BiFunction<T, S, Optional<R>> biFunction) {
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
