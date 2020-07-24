package org.smoothbuild.util;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

public class Lists {
  @SafeVarargs
  public static <E> List<E> list(E... elements) {
    return Arrays.asList(elements);
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

  public static <E> List<E> sane(List<E> list) {
    return list == null ? new ArrayList<>() : list;
  }
}
