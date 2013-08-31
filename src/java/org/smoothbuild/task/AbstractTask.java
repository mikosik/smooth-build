package org.smoothbuild.task;

import static com.google.common.base.Preconditions.checkState;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractTask implements Task {
  private Object result;
  private boolean resultCalculated;
  private final ImmutableMap<String, Task> dependencies;

  public AbstractTask(Object object, Map<String, Task> dependencies) {
    this.result = object;
    this.resultCalculated = true;
    this.dependencies = ImmutableMap.copyOf(dependencies);
  }

  public AbstractTask(Map<String, Task> dependencies) {
    this.result = null;
    this.resultCalculated = false;
    this.dependencies = ImmutableMap.copyOf(dependencies);
  }

  @Override
  public boolean isResultCalculated() {
    return resultCalculated;
  }

  protected void setResult(Object result) {
    this.result = result;
    this.resultCalculated = true;
  }

  @Override
  public Object result() {
    checkState(resultCalculated, "Result not calculated yet.");
    return result;
  }

  public ImmutableMap<String, Task> dependencies() {
    return dependencies;
  }
}
