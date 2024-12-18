package org.smoothbuild.virtualmachine.evaluate.compute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.virtualmachine.evaluate.step.StepHashes.stepHash;

import jakarta.inject.Inject;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.step.Step;
import org.smoothbuild.virtualmachine.wire.Sandbox;

public class ComputationHashFactory {
  private final Hash sandboxHash;

  @Inject
  public ComputationHashFactory(@Sandbox Hash sandboxHash) {
    this.sandboxHash = sandboxHash;
  }

  public Hash create(Step step, BTuple args) {
    return Hash.of(list(sandboxHash, stepHash(step), args.hash()));
  }
}
