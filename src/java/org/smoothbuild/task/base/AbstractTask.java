package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.plugin.Value;

public abstract class AbstractTask implements Task {
  private final CallLocation callLocation;
  private Value result;
  private boolean isResultCalculated;

  public AbstractTask(CallLocation callLocation) {
    this.callLocation = callLocation;
    this.result = null;
    this.isResultCalculated = false;
  }

  @Override
  public CallLocation location() {
    return callLocation;
  }

  @Override
  public boolean isResultCalculated() {
    return isResultCalculated;
  }

  @Override
  public Value result() {
    checkState(isResultCalculated, "Result not calculated yet.");
    return result;
  }

  protected void setResult(Value result) {
    this.result = result;
    this.isResultCalculated = true;
  }
}
