package org.smoothbuild.vm.task;

import static org.smoothbuild.vm.task.TaskHashes.orderTaskHash;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.plugin.NativeApi;

public class OrderTask extends Task {
  public OrderTask(ArrayTB arrayT) {
    super(arrayT);
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
