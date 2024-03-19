package org.smoothbuild.common.dag;

import org.smoothbuild.common.collect.Maybe;

@FunctionalInterface
public interface MaybeFunction<T, R> {
  public Maybe<R> apply(T t);
}
