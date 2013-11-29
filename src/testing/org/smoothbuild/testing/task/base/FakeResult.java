package org.smoothbuild.testing.task.base;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.task.base.Result;

public class FakeResult implements Result {
  private final SValue value;

  public FakeResult(SValue value) {
    this.value = value;
  }

  @Override
  public SValue value() {
    return value;
  }
}
