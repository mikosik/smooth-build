package org.smoothbuild.virtualmachine.evaluate.task;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.OrderB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.type.value.ArrayTB;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;

public final class OrderTask extends Task {
  public OrderTask(OrderB orderB, TraceB trace) {
    super(orderB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) throws BytecodeException {
    ArrayB array = container
        .factory()
        .arrayBuilder((ArrayTB) outputType())
        .addAll(input.elements())
        .build();
    return new Output(array, container.messages());
  }
}
