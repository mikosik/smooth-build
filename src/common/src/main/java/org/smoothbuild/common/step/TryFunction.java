package org.smoothbuild.common.step;

import org.smoothbuild.common.log.base.Try;

@FunctionalInterface
public interface TryFunction<T, R> {
  public Try<R> apply(T t);
}
