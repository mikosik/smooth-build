package org.smoothbuild.common.function;

@FunctionalInterface
public interface Consumer2<A, B, T extends Throwable> {
  public void accept(A a, B b) throws T;
}
