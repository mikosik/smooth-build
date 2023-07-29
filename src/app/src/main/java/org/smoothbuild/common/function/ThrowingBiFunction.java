package org.smoothbuild.common.function;

@FunctionalInterface
public interface ThrowingBiFunction<T, U, R, E extends Throwable> {
  public R apply(T t, U u) throws E;
}
