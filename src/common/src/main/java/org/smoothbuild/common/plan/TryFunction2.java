package org.smoothbuild.common.plan;

import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;

@FunctionalInterface
public interface TryFunction2<A, B, R> {
  public Try<R> apply(A a, B b);

  public default Label label() {
    return Label.label();
  }
}
