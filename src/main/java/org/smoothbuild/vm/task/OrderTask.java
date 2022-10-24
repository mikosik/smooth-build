package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.execute.TaskKind.ORDER;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.vm.compute.Container;

public final class OrderTask extends Task {
  public OrderTask(TypeB arrayT, TagLoc tagLoc, TraceS trace) {
    super(arrayT, ORDER, tagLoc, trace);
    checkArgument(arrayT instanceof ArrayTB);
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
