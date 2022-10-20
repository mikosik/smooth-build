package org.smoothbuild.vm.compute;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import org.smoothbuild.vm.task.Output;

/**
 * Computation result.
 */
public record ComputationResult(Output output, Exception exception, ResultSource source) {

  public ComputationResult(Output output, ResultSource source) {
    this(output, null, source);
  }

  public ComputationResult(Exception exception, ResultSource source) {
    this(null, exception, source);
  }

  public ComputationResult(Output output, Exception exception, ResultSource source) {
    this.output = output;
    this.exception = exception;
    this.source = requireNonNull(source);
    checkArgument((output == null) != (exception == null));
  }

  public boolean hasOutput() {
    return output != null;
  }

  public boolean hasOutputWithValue() {
    return output != null && output.hasVal();
  }
}
