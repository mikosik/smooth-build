package org.smoothbuild.exec.task;

import org.smoothbuild.exec.comp.ComputationException;
import org.smoothbuild.exec.comp.Output;

/**
 * This class is immutable.
 */
public class TaskResult {
  private final Output output;
  private final ComputationException failure;
  private final boolean isFromCache;

  public TaskResult(Output output, boolean isFromCache) {
    this.output = output;
    this.isFromCache = isFromCache;
    this.failure = null;
  }

  public TaskResult(ComputationException failure) {
    this.failure = failure;
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
    return failure;
  }

  public boolean isFromCache() {
    return isFromCache;
  }

  public boolean hasOutputWithValue() {
    return output != null && output.hasValue();
  }
}
