package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;
import static org.smoothbuild.virtualmachine.evaluate.step.Purity.FAST;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class ConstStep extends Step {
  private final BValue value;

  public ConstStep(BValue value, BTrace trace) {
    super(value, trace);
    this.value = value;
  }

  public BValue value() {
    return value;
  }

  @Override
  public Purity purity(BTuple input) {
    return FAST;
  }

  @Override
  public BOutput run(BTuple input, Container container) throws BytecodeException {
    checkArgument(input.elements().isEmpty());
    return bOutput(value, container.messages());
  }
}
