package org.smoothbuild.common.function;

@FunctionalInterface
public interface Function1<A, R, T extends Throwable> {
  public R apply(A a) throws T;

  public static <A, R, T extends Throwable> Function1<A, R, T> memoizer(
      Function1<A, R, T> function1) {
    return new MemoizingFunction1<>(function1);
  }
}
