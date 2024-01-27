package org.smoothbuild.common.function;

@FunctionalInterface
public interface Function0<R, T extends Throwable> {
  public R apply() throws T;

  public static <R, T extends Throwable> Function0<R, T> memoize(Function0<R, T> function0) {
    return new MemoizingFunction0<>(function0);
  }
}
