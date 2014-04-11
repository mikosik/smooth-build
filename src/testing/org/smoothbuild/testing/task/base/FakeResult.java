package org.smoothbuild.testing.task.base;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.base.Result;

public class FakeResult<T extends SValue> implements Result<T> {
  private final T value;

  public FakeResult(T value) {
    this.value = value;
  }

  @Override
  public T value() {
    return value;
  }
}
