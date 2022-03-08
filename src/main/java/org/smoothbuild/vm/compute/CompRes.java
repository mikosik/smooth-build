package org.smoothbuild.vm.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.vm.algorithm.Output;

/**
 * Computation result.
 */
public record CompRes(Output output, Exception exception, ResSource resSource) {

  public CompRes(Output output, ResSource resSource) {
    this(output, null, resSource);
  }

  public CompRes(Exception exception, ResSource resSource) {
    this(null, exception, resSource);
  }

  public CompRes(Output output, Exception exception, ResSource resSource) {
    this.output = output;
    this.exception = exception;
    this.resSource = requireNonNull(resSource);
    checkArgument((output == null) != (exception == null));
  }

  public boolean hasOutput() {
    return output != null;
  }

  public boolean hasOutputWithValue() {
    return output != null && output.hasValue();
  }
}