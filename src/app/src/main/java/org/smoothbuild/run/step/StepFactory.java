package org.smoothbuild.run.step;

import io.vavr.Tuple0;

public interface StepFactory<T, R> {
  public Step<Tuple0, R> create(T argument);
}
