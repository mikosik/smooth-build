package org.smoothbuild.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lists {
  public static <E> List<E> list(E... elements) {
    return Arrays.asList(elements);
  }

  public static <E> List<E> concat(List<E> list, E element) {
    List<E> result = new ArrayList<>(list);
    result.add(element);
    return result;
  }

  public static <E> List<E> filter(List<E> list, Predicate<? super E> predicate) {
    return list
        .stream()
        .filter(predicate)
        .collect(toList());
  }

  public static <E, R> List<R> map(List<E> list, Function<? super E, ? extends R> function) {
    return list
        .stream()
        .map(function)
        .collect(toList());
  }

  public static <E> List<E> sane(List<E> list) {
    return list == null ? new ArrayList<>() : list;
  }
}
