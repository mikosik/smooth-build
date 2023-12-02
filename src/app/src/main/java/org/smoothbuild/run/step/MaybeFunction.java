package org.smoothbuild.run.step;

import org.smoothbuild.common.collect.Maybe;

@FunctionalInterface
public interface MaybeFunction<T, R> {
  public Maybe<R> apply(T t);
}
