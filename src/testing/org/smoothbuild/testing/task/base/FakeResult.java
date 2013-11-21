package org.smoothbuild.testing.task.base;

import org.smoothbuild.lang.type.Value;
import org.smoothbuild.task.base.Result;

public class FakeResult implements Result {
  private final Value value;

  public FakeResult(Value value) {
    this.value = value;
  }

  @Override
  public Value result() {
    return value;
  }
}
