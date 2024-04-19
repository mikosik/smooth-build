package org.smoothbuild.common.concurrent;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.smoothbuild.common.collect.Maybe;

public interface Promise<T> extends Supplier<T> {
  public Maybe<T> toMaybe();

  public void addConsumer(Consumer<T> consumer);

  public <U> Promise<U> chain(Function<T, U> func);
}
