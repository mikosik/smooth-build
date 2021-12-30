package org.smoothbuild.vm.compute;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.vm.job.algorithm.Output;

public record Computed(Output output, Exception exception, ResSource resSource) {

  public Computed(Output output, Exception exception, ResSource resSource) {
    this.output = output;
    this.exception = exception;
    this.resSource = requireNonNull(resSource);
  }

  public Computed(Output output, ResSource resSource) {
    this(output, null, resSource);
  }

  public Computed(Exception exception, ResSource resSource) {
    this(null, exception, resSource);
  }

  public boolean hasOutput() {
    return output != null;
  }

  public boolean hasOutputWithValue() {
    return output != null && output.hasValue();
  }
}
