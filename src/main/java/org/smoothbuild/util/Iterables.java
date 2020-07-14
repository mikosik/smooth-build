package org.smoothbuild.util;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;

public class Iterables {
  public static <E, R> List<R> map(
      Iterable<E> iterable, Function<? super E, ? extends R> function) {
    return Streams.stream(iterable)
        .map(function)
        .collect(ImmutableList.toImmutableList());
  }
}
