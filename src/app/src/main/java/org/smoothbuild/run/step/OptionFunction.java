package org.smoothbuild.run.step;

import io.vavr.control.Option;

@FunctionalInterface
public interface OptionFunction<T, R> {
  public Option<R> apply(T t);
}
