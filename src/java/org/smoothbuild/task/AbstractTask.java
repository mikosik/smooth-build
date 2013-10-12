package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.message.message.CallLocation;

import com.google.common.hash.HashCode;

public abstract class AbstractTask implements Task {
  private final CallLocation callLocation;
  private Object result;
  private boolean resultCalculated;
  private HashCode hash;

  public AbstractTask(CallLocation callLocation) {
    // TODO fix that
    this(callLocation, HashCode.fromInt(33));
  }

  public AbstractTask(CallLocation callLocation, HashCode hash) {
    this.callLocation = callLocation;
    this.result = null;
    this.resultCalculated = false;
  }

  @Override
  public CallLocation location() {
    return callLocation;
  }

  @Override
  public boolean isResultCalculated() {
    return resultCalculated;
  }

  @Override
  public Object result() {
    checkState(resultCalculated, "Result not calculated yet.");
    return result;
  }

  protected void setResult(Object result) {
    this.result = result;
    this.resultCalculated = true;
  }

  public HashCode hash() {
    return hash;
  }
}
