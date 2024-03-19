package org.smoothbuild.common.dag;

import org.smoothbuild.common.log.base.Try;

@FunctionalInterface
public interface TryFunction0<R> {
  public Try<R> apply();
}
