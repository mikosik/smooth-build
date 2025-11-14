package org.smoothbuild.virtualmachine.evaluate.cache;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.virtualmachine.evaluate.evaluator.OperationHashes.operationHash;

import dagger.Lazy;
import jakarta.inject.Inject;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.dagger.Sandbox;

public class ComputationHashFactory {
  /**
   * SandboxHash is injected as Lazy, so it is not read until
   * org.smoothbuild.cli.layout.SandboxHashProviderInitializer is invoked.
   */
  private final Lazy<Hash> sandboxHash;

  @Inject
  public ComputationHashFactory(@Sandbox Lazy<Hash> sandboxHash) {
    this.sandboxHash = sandboxHash;
  }

  public Hash create(BOperation operation, BTuple subExprValues) {
    return Hash.of(list(sandboxHash.get(), operationHash(operation), subExprValues.hash()));
  }
}
