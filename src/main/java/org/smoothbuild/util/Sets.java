package org.smoothbuild.util;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class Sets {
  public static <T, R> Set<R> map(Collection<T> set, Function<? super T, ? extends R> function) {
    return set
        .stream()
        .map(function)
        .collect(toSet());
  }
}
