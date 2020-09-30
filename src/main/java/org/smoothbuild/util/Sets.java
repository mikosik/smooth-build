package org.smoothbuild.util;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;

public class Sets {
  @SafeVarargs
  public static <E> Set<E> set(E... elements) {
    return Arrays.stream(elements).collect(toSet());
  }

  public static <E, R> ImmutableSet<R> map(
      Collection<E> set, Function<? super E, ? extends R> function) {
    return set
        .stream()
        .map(function)
        .collect(toImmutableSet());
  }

  public static <E> ImmutableSet<E> filter(Collection<E> set, Predicate<? super E> predicate) {
    return set
        .stream()
        .filter(predicate)
        .collect(toImmutableSet());
  }
}
