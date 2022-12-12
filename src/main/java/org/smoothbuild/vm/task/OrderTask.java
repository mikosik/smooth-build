package org.smoothbuild.vm.task;

import org.smoothbuild.bytecode.expr.oper.OrderB;
import org.smoothbuild.bytecode.expr.value.ArrayB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.type.value.ArrayTB;
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
