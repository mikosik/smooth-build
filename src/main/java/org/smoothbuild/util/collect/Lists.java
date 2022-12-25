package org.smoothbuild.util.collect;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Lists {
  @SafeVarargs
  public static <E> ImmutableList<E> list(E... elems) {
    return ImmutableList.copyOf(elems);
  }

  public static <R, S extends R, T extends R> ImmutableList<R> concat(S elem, Iterable<T> list) {
    return ImmutableList.<R>builder()
        .add(elem)
        .addAll(list)
        .build();
  }

  public static <R, S extends R, T extends R> ImmutableList<R> concat(Iterable<S> list, T elem) {
    return ImmutableList.<R>builder()
        .addAll(list)
        .add(elem)
        .build();
  }

  public static <R, S extends R, T extends R> ImmutableList<R> concat(
      Iterable<S> list1, Iterable<T> list2) {
    return ImmutableList.<R>builder()
        .addAll(list1)
        .addAll(list2)
        .build();
  }

  public static <E> ImmutableList<E> skip(int toSkip, Iterable<E> iterable) {
    ImmutableList<E> asList = ImmutableList.copyOf(iterable);
    return asList.subList(toSkip, asList.size());
  }

  public static <E> ImmutableList<E> filter(Iterable<E> iterable, Predicate<? super E> predicate) {
    return stream(iterable)
        .filter(predicate)
        .collect(toImmutableList());
  }

  public static <E, R> ImmutableList<R> map(
      Iterable<E> iterable, Function<? super E, ? extends R> func) {
    return stream(iterable)
        .map(func)
        .collect(toImmutableList());
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

  public static <T, S> boolean allMatch(List<T> listA, List<S> listB,
      BiFunction<T, S, Boolean> predicate) {
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

  public static <T, U> void allMatchOtherwise(
      List<T> listA,
      List<U> listB,
      BiFunction<T, U, Boolean> comparator,
      BiConsumer<Integer, Integer> differentSizeHandler,
      Consumer<Integer> elemsDontMatchHandler) {
    int sizeA = listA.size();
    int sizeB = listB.size();
    if (sizeA != sizeB) {
      differentSizeHandler.accept(sizeA, sizeB);
    }
    for (int i = 0; i < listA.size(); i++) {
      if (!comparator.apply(listA.get(i), listB.get(i))) {
        elemsDontMatchHandler.accept(i);
      }
    }
  }

  public static <E> List<E> sane(List<E> list) {
    return list == null ? new ArrayList<>() : list;
  }

  public static <E> ImmutableList<E> sort(List<E> list, Comparator<? super E> comparator) {
    return list.stream()
        .sorted(comparator)
        .collect(toImmutableList());
  }
}
