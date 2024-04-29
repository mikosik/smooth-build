package org.smoothbuild.virtualmachine.evaluate.step;

import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class OrderStep extends Step {
  public OrderStep(BOrder order, BTrace trace) {
    super(order, trace);
  }

  @Override
  public BOutput run(BTuple input, Container container) throws BytecodeException {
    BArray array = container
        .factory()
        .arrayBuilder((BArrayType) evaluationType())
        .addAll(input.elements())
        .build();
    return bOutput(array, container.messages());
  }
}
