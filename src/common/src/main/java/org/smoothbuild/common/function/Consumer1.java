package org.smoothbuild.common.function;

@FunctionalInterface
public interface Consumer1<A, T extends Throwable> {
  public void accept(A a) throws T;
}
