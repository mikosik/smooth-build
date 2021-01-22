package org.smoothbuild.util;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Lists {
  @SafeVarargs
  public static <E> ImmutableList<E> list(E... elements) {
    return ImmutableList.copyOf(elements);
  }

  public static <E> List<E> concat(List<E> list, E element) {
    List<E> result = new ArrayList<>(list);
    result.add(element);
    return result;
  }

  public static <E> ImmutableList<E> filter(List<E> list, Predicate<? super E> predicate) {
    return list
        .stream()
        .filter(predicate)
        .collect(toImmutableList());
  }

  public static <E, R> ImmutableList<R> map(
      List<E> list, Function<? super E, ? extends R> function) {
    return list
        .stream()
        .map(function)
        .collect(toImmutableList());
  }

  public static <E, R> List<R> mapM(
      List<E> list, Function<? super E, ? extends R> function) {
    return list
        .stream()
        .map(function)
        .collect(toList());
  }

  public static <T, S, R> ImmutableList<R> zip(
      List<T> listA, List<S> listB, BiFunction<T, S, R> biFunction) {
    if (listA.size() != listB.size()) {
      throw new IllegalArgumentException(
          "List sizes differ " + listA.size() + " != " + listB.size() + " .");
    }
    Builder<R> builder = ImmutableList.builder();
    for (int i = 0; i < listA.size(); i++) {
      builder.add(biFunction.apply(listA.get(i), listB.get(i)));
    }
    return builder.build();
  }

  public static <T> boolean allMatch(List<T> listA, List<T> listB,
      BiFunction<T, T, Boolean> predicate) {
    if (listA.size() != listB.size()) {
      return false;
    }
    for (int i = 0; i < listA.size(); i++) {
      if (!predicate.apply(listA.get(i), listB.get(i))) {
        return false;
      }
    }
    return true;
  }

  public static <E> List<E> sane(List<E> list) {
    return list == null ? new ArrayList<>() : list;
  }
}
