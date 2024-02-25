package org.smoothbuild.common.function;

@FunctionalInterface
public interface Function3<A, B, C, R, T extends Throwable> {
  public R apply(A a, B b, C c) throws T;
}
