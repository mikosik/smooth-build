package org.smoothbuild.builtin.util;

public class Predicates {

  public static <T> Predicate<T> alwaysTrue() {
    return new Predicate<T>() {
      @Override
      public boolean test(T value) {
        return true;
      }
    };
  }

  public static <T> Predicate<T> not(final Predicate<T> predicate) {
    return new Predicate<T>() {
      @Override
      public boolean test(T value) {
        return !predicate.test(value);
      }
    };
  }

  public static <T> Predicate<T> and(final Predicate<T> predicate, final Predicate<T> predicate2) {
    return new Predicate<T>() {
      @Override
      public boolean test(T value) {
        return predicate.test(value) && predicate2.test(value);
      }
    };
  }

  public static <T> Predicate<T> equalTo(final T object) {
    return new Predicate<T>() {
      @Override
      public boolean test(T value) {
        if (object == null) {
          return value == null;
        }
        return object.equals(value);
      }
    };
  }

  public static Predicate<String> endsWith(final String suffix) {
    return new Predicate<String>() {
      @Override
      public boolean test(String string) {
        return string.endsWith(suffix);
      }
    };
  }
}
