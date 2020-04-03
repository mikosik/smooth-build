package org.smoothbuild.exec.comp;

/**
 * This class is immutable.
 */
public class MaybeOutput {
  private final Output output;
  private final Exception exception;

  public MaybeOutput(Output output) {
    this.output = output;
    this.exception = null;
  }

  public MaybeOutput(Exception exception) {
    this.exception = exception;
    this.output = null;
  }

  public Output output() {
    return output;
  }

  public boolean hasOutput() {
    return output != null;
  }

  public Exception exception() {
    return exception;
  }

  public boolean hasOutputWithValue() {
    return output != null && output.hasValue();
  }

  @Override
  public String toString() {
    return "TaskResult(output=" + output + ", failure=" + exception + ")";
  }
}
