package org.smoothbuild.common.plan;

import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Try;

@FunctionalInterface
public interface TryFunction0<R> {
  public Try<R> apply();

  public default Label label() {
    return Label.label();
  }
}
