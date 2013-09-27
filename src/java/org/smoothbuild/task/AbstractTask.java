package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkState;

public abstract class AbstractTask implements Task {
  private final String name;
  private Object result;
  private boolean resultCalculated;

  public AbstractTask(String name) {
    this.name = name;
    this.result = null;
    this.resultCalculated = false;
  }

  @Override
  public String name() {
    return name;
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
