package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.message.message.CallLocation;

public abstract class AbstractTask implements Task {
  private final CallLocation callLocation;
  private Object result;
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
  public Object result() {
    checkState(isResultCalculated, "Result not calculated yet.");
    return result;
  }

  protected void setResult(Object result) {
    this.result = result;
    this.isResultCalculated = true;
  }
}
