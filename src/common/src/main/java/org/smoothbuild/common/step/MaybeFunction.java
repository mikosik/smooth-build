package org.smoothbuild.common.step;

import org.smoothbuild.common.collect.Maybe;

@FunctionalInterface
public interface MaybeFunction<T, R> {
  public Maybe<R> apply(T t);
}
