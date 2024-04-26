package org.smoothbuild.virtualmachine.evaluate.compute;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.virtualmachine.evaluate.step.Output;

/**
 * Computation result.
 */
public record ComputationResult(Output output, ResultSource source) {
  public ComputationResult(Output output, ResultSource source) {
    this.output = requireNonNull(output);
    this.source = requireNonNull(source);
  }
}
