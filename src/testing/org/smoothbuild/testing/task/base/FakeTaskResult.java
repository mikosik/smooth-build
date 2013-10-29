package org.smoothbuild.testing.task.base;

import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.base.TaskResult;

public class FakeTaskResult implements TaskResult {
  private final Value value;

  public FakeTaskResult(Value value) {
    this.value = value;
  }

  @Override
  public Value result() {
    return value;
  }
}
