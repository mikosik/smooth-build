package org.smoothbuild.common.function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {
  public R apply(T t) throws E;
}
