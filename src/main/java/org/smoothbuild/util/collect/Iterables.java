package org.smoothbuild.util.collect;

import static com.google.common.collect.Streams.stream;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Function;

public class Iterables {
  public static <T> String toCommaSeparatedString(Iterable<T> list) {
    return toCommaSeparatedString(list, Object::toString);
  }

  public static <T> String toCommaSeparatedString(
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
