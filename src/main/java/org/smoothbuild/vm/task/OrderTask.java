package org.smoothbuild.vm.task;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TraceB;

public final class OrderTask extends Task {
  public OrderTask(OrderB orderB, TraceB trace) {
    super(orderB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) {
    ArrayB array = container
        .factory()
        .arrayBuilder((ArrayTB) outputT())
        .addAll(input.items())
        .build();
    return new Output(array, container.messages());
  }
}
