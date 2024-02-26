package org.smoothbuild.common.step;

import org.smoothbuild.common.tuple.Tuple0;

public interface StepFactory<T, R> {
  public Step<Tuple0, R> create(T argument);
}
