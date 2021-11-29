package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;

public class Sets {
  @SafeVarargs
  public static <E> ImmutableSet<E> set(E... elems) {
    return ImmutableSet.copyOf(elems);
  }

  public static <E, R> ImmutableSet<R> map(
      Collection<E> set, Function<? super E, ? extends R> func) {
    return set
        .stream()
        .map(func)
        .collect(toImmutableSet());
  }

  public static <E> ImmutableSet<E> filter(Collection<E> set, Predicate<? super E> predicate) {
    return set
        .stream()
        .filter(predicate)
        .collect(toImmutableSet());
  }
}
