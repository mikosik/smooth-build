package org.smoothbuild.common.dag;

import org.smoothbuild.common.log.base.Try;

@FunctionalInterface
public interface TryFunction2<A, B, R> {
  public Try<R> apply(A a, B b);
}
