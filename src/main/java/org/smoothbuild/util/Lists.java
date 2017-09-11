package org.smoothbuild.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Lists {
  public static <E> List<E> concat(List<E> list, E element) {
    List<E> result = new ArrayList<>(list);
    result.add(element);
    return result;
  }

  public static <T> List<T> filter(List<T> list, Predicate<? super T> predicate) {
    return list
        .stream()
        .filter(predicate)
        .collect(toList());
  }

  public static <T, R> List<R> map(Collection<T> list, Function<? super T, ? extends R> function) {
    return list
        .stream()
        .map(function)
        .collect(toList());
  }

  public static <T> List<T> sane(List<T> list) {
    return list == null ? new ArrayList<>() : list;
  }
}
