package org.smoothbuild.virtualmachine.evaluate.step;

import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public final class CombineStep extends Step {
  public CombineStep(BCombine combine, Trace trace) {
    super("combine", combine.hash(), combine.evaluationType(), trace);
  }

  @Override
  public BOutput run(BTuple input, Container container) throws BytecodeException {
    return bOutput(input, container.messages());
  }
}
