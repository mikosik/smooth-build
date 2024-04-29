package org.smoothbuild.virtualmachine.evaluate.compute;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.virtualmachine.evaluate.step.BOutput;

/**
 * Computation result.
 */
public record ComputationResult(BOutput bOutput, ResultSource source) {
  public ComputationResult(BOutput bOutput, ResultSource source) {
    this.bOutput = requireNonNull(bOutput);
    this.source = requireNonNull(source);
  }
}
