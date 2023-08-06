package org.smoothbuild.virtualmachine.evaluate.compute;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.virtualmachine.evaluate.task.Output;

/**
 * Computation result.
 */
public record ComputationResult(Output output, ResultSource source) {
  public ComputationResult(Output output, ResultSource source) {
    this.output = requireNonNull(output);
    this.source = requireNonNull(source);
  }
}
