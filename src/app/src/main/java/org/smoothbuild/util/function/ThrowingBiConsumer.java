package org.smoothbuild.util.function;

@FunctionalInterface
public interface ThrowingBiConsumer <T, U, E extends Throwable> {
  public void accept(T t, U u) throws E;
}
