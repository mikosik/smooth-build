package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.message.message.TaskLocation;

public abstract class AbstractTask implements Task {
  private final TaskLocation taskLocation;
  private Object result;
  private boolean resultCalculated;

  public AbstractTask(TaskLocation taskLocation) {
    this.taskLocation = taskLocation;
    this.result = null;
    this.resultCalculated = false;
  }

  @Override
  public TaskLocation location() {
    return taskLocation;
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
