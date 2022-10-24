package org.smoothbuild.vm.compute;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.vm.task.Output;

/**
 * Computation result.
 */
public record ComputationResult(Output output, ResultSource source) {
  public ComputationResult(Output output, ResultSource source) {
    this.output = requireNonNull(output);
    this.source = requireNonNull(source);
  }
}
