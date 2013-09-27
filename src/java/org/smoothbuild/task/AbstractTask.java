package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.function.base.Name;

public abstract class AbstractTask implements Task {
  private final Name name;
  private Object result;
  private boolean resultCalculated;

  public AbstractTask(Name name) {
    this.name = name;
    this.result = null;
    this.resultCalculated = false;
  }

  @Override
  public Name name() {
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
