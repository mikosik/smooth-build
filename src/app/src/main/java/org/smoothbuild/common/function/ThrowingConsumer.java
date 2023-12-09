package org.smoothbuild.common.function;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {
  public void accept(T t) throws E;
}
