package org.smoothbuild.vm.task;

import static org.smoothbuild.vm.execute.TaskKind.ORDER;
import static org.smoothbuild.vm.task.TaskHashes.orderTaskHash;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.ArrayTB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.plugin.NativeApi;

public class OrderTask extends Task {
  public OrderTask(ArrayTB arrayT, LabeledLoc labeledLoc) {
    super(arrayT, ORDER, labeledLoc);
  }

  @Override
  public Hash hash() {
    return orderTaskHash(outputT());
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    ArrayB array = nativeApi
        .factory()
        .arrayBuilder((ArrayTB) outputT())
        .addAll(input.items())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
