package org.smoothbuild.util;

@FunctionalInterface
public interface TriFunction<S, T, U, R> {
  public R apply(S s, T t, U u);
}
