package org.smoothbuild.util.function;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Throwable> {
  public R apply(T t, U u) throws E;
}
