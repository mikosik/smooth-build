package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.ImmutableSortedSet.toImmutableSortedSet;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ImmutableSortedSet;

public class Sets {
  @SafeVarargs
  public static <E> ImmutableSet<E> set(E... elems) {
    return ImmutableSet.copyOf(elems);
  }

  public static <T, E1 extends T, E2 extends T> ImmutableSet<T> union(Set<E1> set1, Set<E2> set2) {
    Builder<T> builder = ImmutableSet.builder();
    builder.addAll(set1);
    builder.addAll(set2);
    return builder.build();
  }

  public static <T, E1 extends T, E2 extends T> ImmutableSet<T> union(Set<E1> set1, E2 elem) {
    Builder<T> builder = ImmutableSet.builder();
    builder.addAll(set1);
    builder.add(elem);
    return builder.build();
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

  public static <E> ImmutableSortedSet<E> sort(Collection<E> set,
      Comparator<? super E> comparator) {
    return set
        .stream()
        .collect(toImmutableSortedSet(comparator));
  }
}
