package org.smoothbuild.util.function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {
  public R apply(T t) throws E;
}
