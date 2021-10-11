package org.smoothbuild.util;

import static org.smoothbuild.util.Lists.map;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

public class Optionals {
  public static <T> Optional<ImmutableList<T>> pullUp(Iterable<Optional<T>> iterable) {
    if (Streams.stream(iterable).anyMatch(Optional::isEmpty)) {
      return Optional.empty();
    } else {
      return Optional.of(map(iterable, Optional::get));
    }
  }
}
