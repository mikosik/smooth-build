package org.smoothbuild.util.collect;

import static org.smoothbuild.util.collect.Lists.map;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

public class Optionals {
  public static <T> Optional<ImmutableList<T>> pullUp(
      Iterable<? extends Optional<? extends T>> iterable) {
    if (Streams.stream(iterable).anyMatch(Optional::isEmpty)) {
      return Optional.empty();
    } else {
      return Optional.of(map(iterable, Optional::get));
    }
  }
}
