package org.smoothbuild.common.function;

@FunctionalInterface
public interface Function2<A, B, R, T extends Throwable> {
  public R apply(A a, B b) throws T;
}
