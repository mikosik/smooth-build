package org.smoothbuild.util.function;

@FunctionalInterface
public interface Function4<S, T, U, V, R> {
  public R apply(S s, T t, U u, V v);
}
