package org.smoothbuild.vm.evaluate.task;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.oper.OrderB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.type.value.ArrayTB;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.execute.TraceB;

public final class OrderTask extends Task {
  public OrderTask(OrderB orderB, TraceB trace) {
    super(orderB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) throws BytecodeException {
    ArrayB array = container
        .factory()
        .arrayBuilder((ArrayTB) outputT())
        .addAll(input.elements())
        .build();
    return new Output(array, container.messages());
  }
}
