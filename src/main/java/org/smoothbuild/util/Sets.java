package org.smoothbuild.util;

import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class Sets {
  public static <T, R> Set<R> map(Set<T> set, Function<? super T, ? extends R> function) {
    return set
        .stream()
        .map(function)
        .collect(toSet());
  }

  public static <T> Set<T> filter(Set<T> set, Predicate<? super T> predicate) {
    return set
        .stream()
        .filter(predicate)
        .collect(toSet());
  }
}
