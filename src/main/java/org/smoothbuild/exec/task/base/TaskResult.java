package org.smoothbuild.exec.task.base;

import org.smoothbuild.exec.comp.ComputationException;
import org.smoothbuild.exec.comp.Output;

/**
 * This class is immutable.
 */
public class TaskResult {
  private final Output output;
  private final ComputationException throwable;
  private final boolean isFromCache;

  public TaskResult(Output output, boolean isFromCache) {
    this.output = output;
    this.isFromCache = isFromCache;
    this.throwable = null;
  }

  public TaskResult(ComputationException failure) {
    this.throwable = failure;
    this.output = null;
    this.isFromCache = false;
  }

  public Output output() {
    return output;
  }

  public boolean hasOutput() {
    return output != null;
  }

  public Exception failure() {
    return throwable;
  }

  public boolean isFromCache() {
    return isFromCache;
  }

  public boolean hasOutputWithValue() {
    return output != null && output.hasValue();
  }

  @Override
  public String toString() {
    return "TaskResult(output=" + output + ", failure=" + throwable + ")";
  }
}
