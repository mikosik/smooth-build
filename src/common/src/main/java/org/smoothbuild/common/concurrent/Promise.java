package org.smoothbuild.common.concurrent;

import java.util.function.Consumer;
import org.smoothbuild.common.collect.Maybe;

public interface Promise<T> {
  public T get();

  public T getBlocking() throws InterruptedException;

  public Maybe<T> toMaybe();

  public void addConsumer(Consumer<T> consumer);
}
