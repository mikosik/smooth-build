package org.smoothbuild.exec.compute;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.exec.base.Output;

public record Computed(Output output, Exception exception, ResultSource resultSource) {

  public Computed(Output output, Exception exception, ResultSource resultSource) {
    this.output = output;
    this.exception = exception;
    this.resultSource = requireNonNull(resultSource);
  }

  public Computed(Output output, ResultSource resultSource) {
    this(output, null, resultSource);
  }

  public Computed(Exception exception, ResultSource resultSource) {
    this(null, exception, resultSource);
  }

  public boolean hasOutput() {
    return output != null;
  }

  public boolean hasOutputWithValue() {
    return output != null && output.hasValue();
  }
}
