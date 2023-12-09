package org.smoothbuild.common.function;

@FunctionalInterface
public interface Function1<A, R, T extends Throwable> {
  public R apply(A a) throws T;
}
