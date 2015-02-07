package org.smoothbuild.builtin.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Utils {

  @SuppressWarnings("unused")
  public static int iterableSize(Iterable<?> iterable) {
    int result = 0;
    for (Object eleme : iterable) {
      result++;
    }
    return result;
  }

  public static boolean isEmptyIterable(Iterable<?> iterable) {
    return !iterable.iterator().hasNext();
  }

  public static <T> Set<T> unmodifiableSet(T... elements) {
    Set<T> set = new HashSet<>(elements.length);
    Collections.addAll(set, elements);
    return Collections.unmodifiableSet(set);
  }

  public static <T> T checkNotNull(T object) {
    if (object == null) {
      throw new NullPointerException();
    }
    return object;
  }

  public static void checkArgument(boolean conditions) {
    if (!conditions) {
      throw new IllegalArgumentException();
    }
  }

  public static void checkArgument(boolean conditions, String message) {
    if (!conditions) {
      throw new IllegalArgumentException(message);
    }
  }
}
