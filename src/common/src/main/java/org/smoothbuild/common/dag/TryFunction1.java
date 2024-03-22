package org.smoothbuild.common.dag;

import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;

@FunctionalInterface
public interface TryFunction1<T, R> {
  public Try<R> apply(T t);

  public default Label label() {
    return Label.label();
  }
}