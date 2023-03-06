package org.smoothbuild.util.concurrent;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Promise<T> extends Supplier<T> {
  public void addConsumer(Consumer<T> consumer);

  public <U> Promise<U> chain(Function<T, U> func);
}
