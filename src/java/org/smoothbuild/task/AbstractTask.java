package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractTask implements Task {
  private Object result;
  private boolean resultCalculated;

  public AbstractTask() {
    this.result = null;
    this.resultCalculated = false;
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
}
