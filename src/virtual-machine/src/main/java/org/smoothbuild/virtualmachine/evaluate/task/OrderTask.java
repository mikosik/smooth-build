package org.smoothbuild.virtualmachine.evaluate.task;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class OrderTask extends Task {
  public OrderTask(BOrder order, BTrace trace) {
    super(order, trace);
  }

  @Override
  public Output run(BTuple input, Container container) throws BytecodeException {
    BArray array = container
        .factory()
        .arrayBuilder((BArrayType) outputType())
        .addAll(input.elements())
        .build();
    return new Output(array, container.messages());
  }
}
