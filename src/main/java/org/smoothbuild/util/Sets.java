package org.smoothbuild.util;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Sets {
  public static <E> Set<E> set(E... elements) {
    return Arrays.stream(elements).collect(toSet());
  }

  public static <E, R> Set<R> map(Set<E> set, Function<? super E, ? extends R> function) {
    return set
        .stream()
        .map(function)
        .collect(toSet());
  }

  public static <E> Set<E> filter(Set<E> set, Predicate<? super E> predicate) {
    return set
        .stream()
        .filter(predicate)
        .collect(toSet());
  }
}
