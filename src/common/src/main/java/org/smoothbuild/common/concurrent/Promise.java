package org.smoothbuild.common.concurrent;

import java.util.function.Consumer;
import java.util.function.Function;
import org.smoothbuild.common.collect.Maybe;

public interface Promise<T> {
  public T get();

  public Maybe<T> toMaybe();

  public void addConsumer(Consumer<T> consumer);

  public <U> Promise<U> chain(Function<T, U> func);
}
