package org.smoothbuild.virtualmachine.evaluate.step;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class CombineStep extends Step {
  public CombineStep(BCombine combine, BTrace trace) {
    super(combine, trace);
  }

  @Override
  public Output run(BTuple input, Container container) throws BytecodeException {
    return new Output(input, container.messages());
  }
}
