package org.smoothbuild.common.collect;

import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.common.collect.Lists.list;

import java.util.Iterator;
import java.util.function.Function;

public class Iterables {
  public static Iterable<Integer> intIterable(int firstElement) {
    return () -> new Iterator<>() {
      private int next = firstElement;

      @Override
      public boolean hasNext() {
        return true;
      }

      @Override
      public Integer next() {
        return next++;
      }
    };
  }

  public static <T> String joinWithCommaToString(Iterable<T> list) {
    return joinWithCommaToString(list, Object::toString);
  }

  public static <T> String joinWithCommaToString(
      Iterable<T> list, Function<? super T, String> func) {
    return joinToString(list, func, ",");
  }

  public static String joinToString(String delimiter, Object... elems) {
    return joinToString(list(elems), Object::toString, delimiter);
  }

  public static <T> String joinToString(Iterable<T> list, String delimiter) {
    return joinToString(list, Object::toString, delimiter);
  }

  public static <T> String joinToString(
      Iterable<T> list, Function<? super T, String> func, String delimiter) {
    return stream(list).map(func).collect(joining(delimiter));
  }
}
